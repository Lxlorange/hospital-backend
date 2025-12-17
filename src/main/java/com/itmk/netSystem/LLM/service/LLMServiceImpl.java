package com.itmk.netSystem.LLM.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmk.netSystem.LLM.entity.LLMstore;
import com.itmk.netSystem.LLM.mapper.LLMMapper;
import com.itmk.netSystem.LLM.util.ChatPayloadBuilder;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.service.setWorkService;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.teamDepartment.service.teamDepartmentService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LLMServiceImpl extends ServiceImpl<LLMMapper, LLMstore> implements LLMService {
    @Autowired
    private teamDepartmentService departmentService;
    
    @Autowired
    private userWebService userService;
    
    @Autowired
    private setWorkService scheduleService;

    @Value("${llm.siliconflow.apiKey:}")
    private String apiKey;

    @Value("${llm.siliconflow.model:Qwen/Qwen3-30B-A3B-Thinking-2507}")
    private String model;

    @Value("${llm.defaultSystemPrompt:你是医院后台的智能助手，只回答与本系统功能和使用相关的问题；拒绝提供医疗建议或与系统无关的信息。回答准确、简洁，面向用户实际操作场景。}")
    private String defaultSystemPrompt;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getLoads(String message) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("未配置 LLM API Key");
        }
        String systemPrompt = defaultSystemPrompt + getHospitalContext();
        String finalMessage = message;


        ChatPayloadBuilder builder = new ChatPayloadBuilder(model, systemPrompt, finalMessage, 0.3);
        String payload = builder.build();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.siliconflow.cn/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new IllegalStateException("调用 LLM 接口失败", e);
        }
    }
    @Override
    public String getCommonQuestionLoads(Long id) throws IllegalArgumentException {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("未配置 LLM API Key");
        }
        String systemPrompt = defaultSystemPrompt;
        String finalMessage = "";
        if (id != null) {
            LLMstore store = this.getById(id);
            if (store != null) {
                if (store.getPrompt() != null && !store.getPrompt().trim().isEmpty()) {
                    systemPrompt = store.getPrompt();
                }
                if (store.getMessage() != null && !store.getMessage().trim().isEmpty()) {
                    finalMessage = store.getMessage();
                }
            }
            else{
                throw new IllegalArgumentException("没有找到对应问题！");
            }
        }
        systemPrompt = systemPrompt + getHospitalContext();
        ChatPayloadBuilder builder = new ChatPayloadBuilder(model, systemPrompt, finalMessage, 0.3);
        String payload = builder.build();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.siliconflow.cn/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new IllegalStateException("调用 LLM 接口失败", e);
        }
    }

    private String getHospitalContext() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("\n\n【医院实时运行数据】\n");

            // Departments
            List<Department> depts = departmentService.list();
            Map<Integer, String> deptMap = null;
            if (depts != null && !depts.isEmpty()) {
                deptMap = depts.stream().collect(Collectors.toMap(Department::getDeptId, Department::getDeptName));
                sb.append("科室列表: ");
                sb.append(depts.stream().map(Department::getDeptName).collect(Collectors.joining(", ")));
                sb.append("\n");
            }

            // Doctors (SysUser where jobTitle is not null)
            List<SysUser> doctors = userService.list(new QueryWrapper<SysUser>()
                .isNotNull("job_title")
                .ne("job_title", ""));
            
            if (doctors != null && !doctors.isEmpty()) {
                sb.append("医生信息 (部分): \n");
                final Map<Integer, String> finalDeptMap = deptMap;
                doctors.stream()
                    .filter(SysUser::isEnabled)
                    .limit(20)
                    .forEach(doc -> {
                        String dName = (finalDeptMap != null && doc.getDeptId() != null) ? finalDeptMap.get(doc.getDeptId()) : "未知科室";
                        sb.append(String.format("- %s (%s, %s): 擅长%s\n", 
                            doc.getNickName(), 
                            dName, 
                            doc.getJobTitle(), 
                            doc.getGoodAt() != null ? doc.getGoodAt() : "无"));
                    });
            }

            // Schedules (Next 3 days)
            LocalDate today = LocalDate.now();
            LocalDate threeDaysLater = today.plusDays(3);
            List<ScheduleDetail> schedules = scheduleService.list(new QueryWrapper<ScheduleDetail>()
                    .ge("times", today)
                    .le("times", threeDaysLater)
                    .orderByAsc("times"));
            
            if (schedules != null && !schedules.isEmpty()) {
                sb.append("未来3天排班情况 (部分): \n");
                 schedules.stream().limit(30).forEach(s ->
                     sb.append(String.format("- %s %s: %s 剩余:%d\n", 
                        s.getTimes(), 
                        s.getTimeSlot() != null && s.getTimeSlot() == 0 ? "上午" : "下午",
                        s.getDoctorName(),
                        s.getLastAmount()
                     ))
                 );
            }
        } catch (Exception e) {
            // Ignore errors to ensure LLM service stability
            System.err.println("Error fetching hospital context: " + e.getMessage());
        }
        return sb.toString();
    }

    private String escape(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
