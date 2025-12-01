package com.itmk.netSystem.leaveRequest.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.netSystem.leaveRequest.entity.LeaveRequest;
import com.itmk.netSystem.leaveRequest.entity.LeaveRequestListItem;
import com.itmk.netSystem.leaveRequest.service.LeaveRequestService;
import com.itmk.netSystem.schedule.service.ScheduleService;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 医生请假申请接口
 */
@RestController
@RequestMapping("/api/leaveRequest")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private userWebService userWebService;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 1. 申请请假接口
     * POST /api/requestLeave
     */
    @PostMapping("/requestLeave")
    public ResultVo requestLeave(@RequestBody LeaveRequest req) {
        // 校验医生ID
        String doctorIdStr = req.getDoctorId();
        if (StringUtils.isEmpty(doctorIdStr)) {
            return ResultUtils.error("参数错误：缺少医生ID");
        }
        Long doctorIdLong;
        try {
            doctorIdLong = Long.valueOf(doctorIdStr);
        } catch (Exception e) {
            return ResultUtils.error("医生ID格式错误");
        }

        // 校验医生是否存在
        SysUser user = userWebService.getById(doctorIdLong);
        if (user == null) {
            return ResultUtils.error("医生不存在");
        }

        // 校验排班ID列表
        if (req.getScheduleId() == null) {
            return ResultUtils.error("参数错误：请传入待请假的排班ID列表 scheduleIds");
        }

        Long scheduleId = req.getScheduleId();
        if (scheduleId == null) {
            return ResultUtils.error("存在空的排班ID");
        }
        ScheduleDetail detail = scheduleService.getByScheduleId(scheduleId);
        if (detail == null) {
            return ResultUtils.error("无效的排班ID：" + scheduleId);
        }
        // 归属校验：排班必须属于该医生
        if (detail.getDoctorId() == null || !detail.getDoctorId().equals(doctorIdLong.intValue())) {
            return ResultUtils.error("排班ID " + scheduleId + " 不属于医生 " + doctorIdStr);
        }
        // 状态校验：仅允许在正常上班的排班上发起请假申请
        if (detail.getType() != null && !"1".equals(detail.getType())) {
            return ResultUtils.error("排班ID " + scheduleId + " 非正常上班状态，无法申请请假");
        }

        // 提交申请
        boolean ok = leaveRequestService.submit(req);
        if (!ok) {
            return ResultUtils.error("unknown error");
        }

        return ResultUtils.success("申请成功");
    }

    /**
     * 2. 获取请假申请列表
     * GET /api/leaveRequest/list
     */
    @GetMapping("/list")
    public ResultVo list(@RequestParam(defaultValue = "1") Long currentPage,
                         @RequestParam(defaultValue = "10") Long pageSize,
                         @RequestParam(required = false) String status,
                         @RequestParam(required = false) String doctorId) {
        IPage<LeaveRequest> page = new Page<>(currentPage, pageSize);
        QueryWrapper<LeaveRequest> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(status)) {
            query.lambda().eq(LeaveRequest::getStatus, status);
        }
        if (StringUtils.isNotEmpty(doctorId)) {
            query.lambda().eq(LeaveRequest::getDoctorId, doctorId);
        }
        query.lambda().orderByDesc(LeaveRequest::getCreateTime);

        IPage<LeaveRequest> result = leaveRequestService.page(page, query);

        // 映射为前端期望的 records 结构
        List<LeaveRequestListItem> items = new ArrayList<>();
        for (LeaveRequest req : result.getRecords()) {
            LeaveRequestListItem item = new LeaveRequestListItem();
            item.setRequestId(req.getRequestId());
            item.setDoctorId(req.getDoctorId());
            // 昵称
            try {
                if (StringUtils.isNotEmpty(req.getDoctorId())) {
                    Long doctorIdLong = Long.valueOf(req.getDoctorId());
                    SysUser user = userWebService.getById(doctorIdLong);
                    if (user != null) {
                        item.setNickName(user.getNickName());
                    }
                }
            } catch (Exception ignored) {}

            // 从排班获取日期与时段
            if (req.getScheduleId() != null) {
                ScheduleDetail detail = scheduleService.getByScheduleId(req.getScheduleId());
                if (detail != null) {
                    String dateStr = detail.getTimes() != null ? detail.getTimes().toString() : null;
                    item.setStartDate(dateStr);
                    item.setEndDate(dateStr);
                    String slotStr = detail.getTimeSlot() != null ? detail.getTimeSlot().toString() : null;
                    item.setStartTime(slotStr);
                    item.setEndTime(slotStr);
                }
            }

            item.setReason(req.getReason());
            item.setStatus(req.getStatus());
            item.setReviewComment(req.getReviewComment());
            item.setReviewerId(req.getReviewerId());
            item.setReviewTime(req.getReviewTime());
            item.setCreateTime(req.getCreateTime());
            items.add(item);
        }

        Page<LeaveRequestListItem> itemPage = new Page<>(currentPage, pageSize);
        itemPage.setRecords(items);
        itemPage.setTotal(result.getTotal());
        itemPage.setCurrent(result.getCurrent());
        itemPage.setSize(result.getSize());

        return ResultUtils.success("success", itemPage);
    }

    /**
     * 3. 审批请假
     * POST /api/leaveRequest/approve
     */
    @PostMapping("/approve")
    public ResultVo approve(@RequestBody Map<String, Object> body) {
        Object reqIdObj = body.get("requestId");
        Object statusObj = body.get("status");
        String reviewComment = (String) body.get("reviewComment");
        if (reqIdObj == null || statusObj == null) {
            return ResultUtils.error("参数错误");
        }
        Long requestId = Long.valueOf(reqIdObj.toString());
        String status = statusObj.toString(); // "1"=通过，"2"=拒绝

        // 可选：记录审批人（当前登录用户）
        Long reviewerId = null;
        // 通过用户名查当前用户ID（若存在）
        // 简化：不强制校验管理员权限，保持接口易用性

        boolean ok = leaveRequestService.approve(requestId, status, reviewComment, reviewerId);
        if (!ok) {
            return ResultUtils.error("审核失败");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("requestId", requestId);
        data.put("reviewComment", reviewComment);
        return ResultUtils.success("success", data);
    }
}