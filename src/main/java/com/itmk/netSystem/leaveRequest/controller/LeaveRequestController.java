package com.itmk.netSystem.leaveRequest.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.netSystem.leaveRequest.entity.LeaveRequest;
import com.itmk.netSystem.leaveRequest.service.LeaveRequestService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
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

    /**
     * 1. 申请请假接口
     * POST /api/requestLeave
     */
    @PostMapping("/requestLeave")
    public ResultVo requestLeave(@RequestBody LeaveRequest req) {
        if (StringUtils.isEmpty(req.getDoctorId()) || StringUtils.isEmpty(req.getNickName())
                || StringUtils.isEmpty(req.getStartDate()) || StringUtils.isEmpty(req.getEndDate())
                || StringUtils.isEmpty(req.getStartTime()) || StringUtils.isEmpty(req.getEndTime())) {
            return ResultUtils.error("参数不完整");
        }

        boolean ok = leaveRequestService.submit(req);
        if (!ok) {
            return ResultUtils.error("申请失败");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("requestId", req.getRequestId());
        data.put("status", "0");
        return ResultUtils.success("申请成功", data);
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
        return ResultUtils.success("success", result);
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