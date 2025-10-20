package com.itmk.netSystem.schedule.service.implement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.itmk.netSystem.schedule.entity.*;
import com.itmk.netSystem.schedule.mapper.*;
import com.itmk.netSystem.schedule.service.ScheduleService;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.mapper.setWorkMapper;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.mapper.userWebMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    // 注入新创建的四个Mapper
    @Autowired private ScheduleTemplateMapper templateMapper;
    @Autowired private TemplateSlotMapper templateSlotMapper;
    @Autowired private setWorkMapper scheduleDetailMapper;
    @Autowired private userWebMapper SysUserMapper;

    // 注入用于关联查询的Mapper
    @Autowired private com.itmk.netSystem.userWeb.mapper.userWebMapper sysUserMapper;
    @Autowired private com.itmk.netSystem.teamDepartment.mapper.teamDepartmentMapper departmentMapper;

    //获得某个医生的所有排班模板
    @Override
    public List<ScheduleTemplate> getDoctorTemplates(Long doctorId) {
        List<ScheduleTemplate> templates = templateMapper.selectList(
                new QueryWrapper<ScheduleTemplate>().eq("doctor_id", doctorId)
        );
        // 为每个模板查询并填充其关联的号源列表
        templates.forEach(template -> {
            List<TemplateSlot> slots = templateSlotMapper.selectList(
                    new QueryWrapper<TemplateSlot>().eq("template_id", template.getTemplateId())
            );
            template.setSlots(slots);
        });
        return templates;
    }

    /**
     * 3.2 保存医生排班模板 (后端自动计算价格版)
     */
    @Override
    @Transactional
    public void saveDoctorTemplates(Long doctorId, List<Map<String, Object>> request) {
        // 2. 在所有操作开始前，先获取医生的信息，特别是职称
        SysUser doctor = sysUserMapper.selectById(doctorId);
        if (doctor == null) {
            throw new RuntimeException("未找到ID为 " + doctorId + " 的医生信息");
        }

        // 3. 删除该医生的所有旧模板及关联的号源 (逻辑不变)
        List<ScheduleTemplate> oldTemplates = templateMapper.selectList(
                new QueryWrapper<ScheduleTemplate>().eq("doctor_id", doctorId)
        );
        if (!oldTemplates.isEmpty()) {
            List<Long> oldTemplateIds = oldTemplates.stream()
                    .map(ScheduleTemplate::getTemplateId).collect(Collectors.toList());
            templateSlotMapper.delete(new QueryWrapper<TemplateSlot>().in("template_id", oldTemplateIds));
            templateMapper.deleteBatchIds(oldTemplateIds);
        }

        // 4. 插入新的模板数据 (逻辑修改)
        for (Map<String, Object> reqMap : request) {
            ScheduleTemplate newTemplate = new ScheduleTemplate();
            newTemplate.setDoctorId(doctorId);
            newTemplate.setDayOfWeek((Integer) reqMap.get("dayOfWeek"));
            newTemplate.setTimeSlot((Integer) reqMap.get("timeSlot"));
            templateMapper.insert(newTemplate);

            List<Map<String, Object>> slots = (List<Map<String, Object>>) reqMap.get("slots");
            for (Map<String, Object> slotMap : slots) {
                TemplateSlot newSlot = new TemplateSlot();
                newSlot.setTemplateId(newTemplate.getTemplateId());
                String slotType = (String) slotMap.get("slotType");
                newSlot.setSlotType(slotType);
                newSlot.setTotalAmount((Integer) slotMap.get("totalAmount"));


                BigDecimal price = calculatePrice(slotType, doctor.getJobTitle());
                newSlot.setPrice(price);

                templateSlotMapper.insert(newSlot);
            }
        }
    }


    //根据模板在一段日期内生成排班实例
    @Override
    @Transactional
    public void generateInstances(Map<String, String> request) {
        LocalDate startDate = LocalDate.parse(request.get("startDate"));
        LocalDate endDate = LocalDate.parse(request.get("endDate"));
        List<ScheduleTemplate> allTemplates = templateMapper.selectList(null);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final int dayOfWeek = date.getDayOfWeek().getValue();
            final LocalDate currentDate = date;

            allTemplates.stream()
                    .filter(t -> t.getDayOfWeek() == dayOfWeek)
                    .forEach(template -> {
                        List<TemplateSlot> templateSlots = templateSlotMapper.selectList(
                                new QueryWrapper<TemplateSlot>().eq("template_id", template.getTemplateId())
                        );

                        for (TemplateSlot ts : templateSlots) {
                            boolean exists = scheduleDetailMapper.exists(new QueryWrapper<ScheduleDetail>()
                                    .eq("doctor_id", template.getDoctorId())
                                    .eq("times", currentDate)
                                    .eq("time_slot", template.getTimeSlot())
                                    .eq("level_name", ts.getSlotType())
                            );

                            if (!exists) {
                                SysUser doctor = sysUserMapper.selectById(template.getDoctorId());

                                ScheduleDetail instance = new ScheduleDetail();
                                instance.setDoctorId(template.getDoctorId().intValue());
                                instance.setDoctorName(doctor != null ? doctor.getNickName() : "未知医生");
                                instance.setTimes(currentDate);
                                instance.setWeek(currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CHINESE));

                                instance.setTimeSlot(template.getTimeSlot());
                                instance.setLevelName(ts.getSlotType());
                                instance.setPrice(ts.getPrice());
                                instance.setAmount(ts.getTotalAmount());
                                instance.setLastAmount(ts.getTotalAmount());

                                instance.setType("1");

                                scheduleDetailMapper.insert(instance);
                            }
                        }
                    });
        }
    }

    @Override
    public List<ScheduleDetail> findInstances(String startDate, String endDate, Long deptId, Long doctorId) {
        // 直接查询 schedule_detail 表，并进行连表
        MPJLambdaWrapper<ScheduleDetail> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.between(ScheduleDetail::getTimes, startDate, endDate)
                .leftJoin(SysUser.class, SysUser::getUserId, ScheduleDetail::getDoctorId)
                .leftJoin(Department.class, Department::getDeptId, SysUser::getDeptId)
                .selectAll(ScheduleDetail.class)
                .selectAs(Department::getDeptName, ScheduleDetail::getDeptName);

        if (doctorId != null) {
            queryWrapper.eq(ScheduleDetail::getDoctorId, doctorId);
        }
        if (deptId != null) {
            queryWrapper.eq(SysUser::getDeptId, deptId);
        }

        return scheduleDetailMapper.selectJoinList(ScheduleDetail.class, queryWrapper);
    }

    //更新排班实例状态
    @Override
    public void updateInstanceStatus(Long instanceId, Map<String, Integer> request) {
        // 前端传来的 status: 1=正常, 2=停诊
        // 需要将其转换为后端的 type: '1'=上班, '0'=休息
        Integer status = request.get("status");
        if (status != null) {
            ScheduleDetail detail = new ScheduleDetail();
            detail.setScheduleId(instanceId.intValue());
            String typeValue = status == 1 ? "1" : "0";
            detail.setType(typeValue);

            scheduleDetailMapper.updateById(detail);
        }
    }

    /**
     * 根据号别类型和医生职称计算价格的私有辅助方法
     * @param slotType 号别类型 (如 "普通号", "专家号")
     * @param jobTitle 医生职称 (如 "主任医师")
     * @return 计算出的价格
     */
    private BigDecimal calculatePrice(String slotType, String jobTitle) {
        switch (slotType) {
            case "普通号":
                return new BigDecimal("50.00");
            case "专家号":
                if ("主任医师".equals(jobTitle)) {
                    return new BigDecimal("80.00");
                } else if ("知名主任医师".equals(jobTitle)) {
                    return new BigDecimal("100.00");
                } else { // 默认为副主任医师或其他专家
                    return new BigDecimal("60.00");
                }
            case "特需号": // 根据您的规则
                return new BigDecimal("500.00");
            case "国际医疗":
                return new BigDecimal("1000.00");
            default:
                // 对于未知的号别类型，可以抛出异常或返回一个默认值，这里抛出异常更严谨
                throw new IllegalArgumentException("不支持的号别类型: " + slotType);
        }
    }
}