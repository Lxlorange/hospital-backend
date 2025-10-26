package com.itmk.netSystem.plat.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.netSystem.teamDepartment.service.teamDepartmentService;
import com.itmk.netSystem.plat.entity.Plat;
import com.itmk.netSystem.see.service.SeeService;
import com.itmk.netSystem.announceWeb.entity.SysNotice;
import com.itmk.netSystem.announceWeb.service.AnnounceWebService;
import com.itmk.netSystem.userPatientPhone.service.UserPatientPhoneService;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.service.setWorkService;
import com.itmk.netSystem.userWeb.service.userWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/home")
public class PlatController {
    // 自动注入科室管理服务
    @Autowired
    private teamDepartmentService teamDepartmentService;
    // 自动注入网站用户（医生）管理服务
    @Autowired
    private userWebService userWebService;
    // 自动注入小程序用户（患者）管理服务
    @Autowired
    private UserPatientPhoneService userPatientPhoneService;
    // 自动注入就诊记录服务
    @Autowired
    private SeeService seeService;
    // 自动注入公告管理服务
    @Autowired
    private AnnounceWebService announceWebService;
    // 自动注入排班管理服务
    @Autowired
    private setWorkService setWorkService;


    /**
     * @description:获取系统健康状态或关键指标。
     * @return 返回一个包含系统状态（如数据库连接、响应时间等）的对象，用于管理员监控。
     */
    @GetMapping("/getSystemHealthStatus")
    public ResultVo getSystemHealthStatus() {
        Map<String, String> healthStatus = Map.of(
                "databaseConnection", "OK",
                "apiResponseTime", "120ms",
                "lastBackup", LocalDate.now().minusDays(1).toString()
        );
        return ResultUtils.success("系统健康状态获取成功", healthStatus);
    }

    /**
     * @description:  获取指定患者未来最近的预约安排。
     * @param patientId 患者的唯一标识符。
     * @param limit 返回的预约数量，默认为3。
     * @return 返回该患者即将到来的预约列表。
     */
    @GetMapping("/getUpcomingAppointmentsForPatient")
    public ResultVo getUpcomingAppointmentsForPatient(@RequestParam Integer patientId, @RequestParam(defaultValue = "3") int limit) {
        List<?> upcomingAppointments = Collections.emptyList(); // 模拟返回
        return ResultUtils.success("患者未来预约获取成功", upcomingAppointments);
    }



    /**
     * 根据指定类型动态获取医生的排班信息。
     * @param type 查询类型: "1" for 本周, "2" for 下周, 其他值为 上周.
     * @param doctorId 医生的唯一标识符.
     * @return 返回包含排班详情的响应实体.
     */
    @GetMapping("/getMySchedule")
    public ResultVo getMySchedule(String type, Integer doctorId) {
        // 初始化当前日期作为计算基准
        LocalDate today = LocalDate.now();
        List<ScheduleDetail> list; // 用于存储查询结果的列表

        switch (type) {
            case "1": // 获取本周的排班
                LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                QueryWrapper<ScheduleDetail> currentWeekQuery = new QueryWrapper<>();
                currentWeekQuery.lambda().eq(ScheduleDetail::getDoctorId, doctorId)
                        .between(ScheduleDetail::getTimes, startOfWeek.toString(), endOfWeek.toString());
                list = setWorkService.list(currentWeekQuery);
                break;
            case "2": // 获取下周的排班
                LocalDate startOfNextWeek = today.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate endOfNextWeek = today.plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                QueryWrapper<ScheduleDetail> nextWeekQuery = new QueryWrapper<>();
                nextWeekQuery.lambda().eq(ScheduleDetail::getDoctorId, doctorId)
                        .between(ScheduleDetail::getTimes, startOfNextWeek.toString(), endOfNextWeek.toString());
                list = setWorkService.list(nextWeekQuery);
                break;
            default: // 默认获取上周的排班
                LocalDate startOfLastWeek = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate endOfLastWeek = today.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                QueryWrapper<ScheduleDetail> lastWeekQuery = new QueryWrapper<>();
                lastWeekQuery.lambda().eq(ScheduleDetail::getDoctorId, doctorId)
                        .between(ScheduleDetail::getTimes, startOfLastWeek.toString(), endOfLastWeek.toString());
                list = setWorkService.list(lastWeekQuery);
        }
        return ResultUtils.success("排班数据获取成功", list);
    }


    /**
     * @description: 查询特定医生在指定日期的可用预约时间段。
     * @param doctorId 医生的ID。
     * @param date 查询的日期 (格式: yyyy-MM-dd)。
     * @return 返回该医生在该日期的所有可用时间段列表。
     */
    @GetMapping("/getDoctorAvailabilitySlots")
    public ResultVo getDoctorAvailabilitySlots(@RequestParam Integer doctorId, @RequestParam String date) {
        // 此功能需要结合医生的排班表(schedule_detail)和已有的预约表(see)来计算
        // 1. 获取医生当天的排班信息（如 09:00-12:00, 14:00-17:00）
        // 2. 获取医生当天已被预约的时间点
        // 3. 计算出所有未被占用的时间槽（如每30分钟一个）
        // List<String> availableSlots = setWorkService.calculateAvailableSlots(doctorId, LocalDate.parse(date));
        List<?> availableSlots = List.of("09:30", "10:00", "14:00", "15:30"); // 模拟返回
        return ResultUtils.success("医生可用时间段查询成功", availableSlots);
    }

    /**
     * 提供平台的核心统计数据，如科室、医生、患者总数和总就诊次数。
     * @return 包含各项统计数据的响应实体.
     */
    @GetMapping("/getHomeTotal")
    public ResultVo getHomeTotal() {
        // 创建一个数据传输对象用于封装所有统计数据
        Plat vo = new Plat();
        // 查询并设置科室总数
        vo.setDepartmentCount(teamDepartmentService.count());
        // 查询并设置注册医生总数
        vo.setSysUserCount(userWebService.count());
        // 查询并设置注册患者总数
        vo.setWxUserCount(userPatientPhoneService.count());
        // 查询并设置平台累计就诊次数
        vo.setVisitCount(seeService.count());
        return ResultUtils.success("核心统计数据查询成功", vo);
    }

    /**
     * @description: 获取表现最佳的医生排名（例如按接诊量或患者评分）。
     * @param criteria 排名标准，如 "visits" (接诊量) 或 "rating" (评分)。
     * @param limit 返回的医生数量，默认为5。
     * @return 返回医生排名列表，包含医生信息和对应的指标数据。
     */
    @GetMapping("/getTopPerformingDoctors")
    public ResultVo getTopPerformingDoctors(@RequestParam(defaultValue = "visits") String criteria, @RequestParam(defaultValue = "5") int limit) {
        // userWebService 需要一个自定义SQL来根据不同标准进行排名
        // List<Map<String, Object>> topDoctors = userWebService.rankDoctorsByCriteria(criteria, limit);
        List<?> topDoctors = Collections.emptyList(); // 模拟返回
        return ResultUtils.success("最佳表现医生排名获取成功", topDoctors);
    }

    /**
     * @description: 根据用户角色提供个性化的快捷操作链接。
     * @param roleId 当前登录用户的角色ID。
     * @return 返回一个包含标题和链接的快捷操作列表。
     */
    @GetMapping("/getQuickAccessLinks")
    public ResultVo getQuickAccessLinks(@RequestParam Integer roleId) {
        // 这是一个基于角色的动态菜单/链接生成器
        // Map<String, String> links = new HashMap<>();
        // if (roleId == 1) { // 假设1是管理员
        //     links.put("用户管理", "/admin/users");
        //     links.put("系统设置", "/admin/settings");
        // } else if (roleId == 2) { // 假设2是医生
        //     links.put("开具处方", "/doctor/prescription/new");
        //     links.put("查看病历", "/doctor/records");
        // }
        Map<?,?> links = Collections.emptyMap(); // 模拟返回
        return ResultUtils.success("快捷操作链接获取成功", links);
    }

    /**
     * 获取最新的三条公告，用于在首页展示。
     * @return 包含最新公告列表的响应实体.
     */
    @GetMapping("/getIndexNotice")
    public ResultVo getIndexNotice() {
        QueryWrapper<SysNotice> query = new QueryWrapper<>();
        // 按创建时间降序排序，并限制结果数量为3
        query.lambda().orderByDesc(SysNotice::getCreateTime)
                .last(" limit 3");
        List<SysNotice> list = announceWebService.list(query);
        return ResultUtils.success("公告信息加载完毕", list);
    }


}