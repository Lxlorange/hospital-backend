package com.itmk.netSystem.schedule.controller;

import com.itmk.netSystem.schedule.entity.ScheduleTemplate;
import com.itmk.netSystem.schedule.service.ScheduleService;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "新版排班(模板与实例)管理接口")
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation("3.1 获取医生排班模板")
    @GetMapping("/template/{doctorId}")
    public ResultVo getDoctorTemplates(@ApiParam(value = "医生ID", required = true) @PathVariable Long doctorId) {
        List<ScheduleTemplate> data = scheduleService.getDoctorTemplates(doctorId);
        return ResultUtils.success("查询成功", data);
    }

    @ApiOperation("3.2 保存医生排班模板")
    @PostMapping("/template/{doctorId}")
    public ResultVo saveDoctorTemplates(
            @ApiParam(value = "医生ID", required = true) @PathVariable Long doctorId,
            @RequestBody List<Map<String, Object>> request) {
        scheduleService.saveDoctorTemplates(doctorId, request);
        return ResultUtils.success("保存成功");
    }

    @ApiOperation("3.3 根据模板生成排班实例")
    @PostMapping("/instance/generate")
    public ResultVo generateInstances(@RequestBody Map<String, String> request) {
        scheduleService.generateInstances(request);
        return ResultUtils.success("排班生成成功");
    }

    @ApiOperation("3.4 查询排班实例列表")
    @GetMapping("/instance")
    public ResultVo findInstances(
            @ApiParam(value = "开始日期 (YYYY-MM-DD)", required = true) @RequestParam String startDate,
            @ApiParam(value = "结束日期 (YYYY-MM-DD)", required = true) @RequestParam String endDate,
            @ApiParam(value = "科室ID (可选)") @RequestParam(required = false) Long deptId,
            @ApiParam(value = "医生ID (可选)") @RequestParam(required = false) Long doctorId
    ) {
        List<ScheduleDetail> data = scheduleService.findInstances(startDate, endDate, deptId, doctorId);
        return ResultUtils.success("查询成功", data);
    }

    @ApiOperation("3.5 更新排班实例状态")
    @PutMapping("/instance/{instanceId}/status")
    public ResultVo updateInstanceStatus(
            @ApiParam(value = "实例ID", required = true) @PathVariable Long instanceId,
            @RequestBody Map<String, Integer> request) {
        scheduleService.updateInstanceStatus(instanceId, request);
        return ResultUtils.success("状态更新成功");
    }
}