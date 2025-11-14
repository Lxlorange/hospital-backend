package com.itmk.netSystem.waitlist.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.netSystem.waitlist.entity.WaitlistEntry;
import com.itmk.netSystem.waitlist.service.WaitlistService;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.service.setWorkService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.netSystem.treatpatient.entity.VisitUser;
import com.itmk.netSystem.treatpatient.service.TreatPatientService;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.teamDepartment.service.teamDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/waitlist")
public class WaitlistWebController {

    @Autowired
    private WaitlistService waitlistService;
    @Autowired
    private setWorkService setWorkService;
    @Autowired
    private userWebService userWebService;
    @Autowired
    private TreatPatientService treatPatientService;
    @Autowired
    private teamDepartmentService teamDepartmentService;

    @GetMapping("/doctorPending")
    public ResultVo doctorPending(@RequestParam("doctorId") Integer doctorId,
                                  @RequestParam(value = "name", required = false) String name) {
        QueryWrapper<WaitlistEntry> q = new QueryWrapper<>();
        q.lambda()
                .eq(WaitlistEntry::getDoctorId, doctorId)
                .eq(WaitlistEntry::getStatus, "pending")
                .orderByAsc(WaitlistEntry::getPriority)
                .orderByAsc(WaitlistEntry::getCreateTime);
        List<WaitlistEntry> list = waitlistService.list(q);

        if (name != null && !name.isEmpty()) {
            list = list.stream().filter(e -> {
                VisitUser v = treatPatientService.getById(e.getVisitUserId());
                return v != null && v.getVisitname() != null && v.getVisitname().contains(name);
            }).collect(Collectors.toList());
        }

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Map<String, Object>> data = new ArrayList<>();
        for (WaitlistEntry e : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", e.getId());
            m.put("scheduleId", e.getScheduleId());
            m.put("userId", e.getUserId());
            m.put("visitUserId", e.getVisitUserId());
            VisitUser v = treatPatientService.getById(e.getVisitUserId());
            m.put("visitname", v != null ? v.getVisitname() : "");
            SysUser d = userWebService.getById(e.getDoctorId());
            m.put("doctorId", e.getDoctorId());
            m.put("doctorName", d != null ? d.getNickName() : "");
            String deptName = "";
            if (d != null && d.getDeptId() != null) {
                Department dept = teamDepartmentService.getById(d.getDeptId());
                if (dept != null) deptName = dept.getDeptName();
            }
            m.put("deptName", deptName);
            ScheduleDetail sd = setWorkService.getById(e.getScheduleId());
            String times = "";
            String timesAreaLabel = "";
            String week = "";
            if (sd != null) {
                try { times = sd.getTimes() == null ? "" : sd.getTimes().format(df); } catch (Exception ignored) {}
                if (sd.getTimeSlot() != null) {
                    timesAreaLabel = "0".equals(String.valueOf(sd.getTimeSlot())) ? "上午" : "下午";
                }
                week = sd.getWeek();
            }
            m.put("times", times);
            m.put("timesAreaLabel", timesAreaLabel);
            m.put("week", week);
            m.put("status", e.getStatus());
            m.put("createTime", e.getCreateTime());
            data.add(m);
        }

        return ResultUtils.success("查询成功", data);
    }
}