package com.itmk.netSystem.doctor.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.netSystem.doctor.entity.DoctorUpdateRequest;
import com.itmk.netSystem.doctor.service.DoctorUpdateRequestService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 医生信息更新申请管理控制器
 */
@RestController
@RequestMapping("/api/doctorUpdateRequest")
public class DoctorUpdateRequestController {

    @Autowired
    private DoctorUpdateRequestService doctorUpdateRequestService;

    @Autowired
    private userWebService userWebService;

    /**
     * 获取待审核的医生信息更新申请列表
     * @param currentPage 当前页
     * @param pageSize 每页大小
     * @param status 状态（可选）
     * @return 申请列表
     */
    @GetMapping("/list")
    public ResultVo getUpdateRequestList(
            @RequestParam(defaultValue = "1") Long currentPage,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String status) {
        // 手动校验是否为管理员
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
        userQuery.lambda().eq(SysUser::getUsername, username);
        SysUser currentUser = userWebService.getOne(userQuery);
        if (currentUser == null || !"1".equals(currentUser.getIsAdmin())) {
            return ResultUtils.error("无权限访问,请联系管理员!");
        }
        
        IPage<DoctorUpdateRequest> page = new Page<>(currentPage, pageSize);
        QueryWrapper<DoctorUpdateRequest> queryWrapper = new QueryWrapper<>();
        
        // 如果指定了状态，则按状态筛选
        if (status != null && !status.isEmpty()) {
            queryWrapper.lambda().eq(DoctorUpdateRequest::getStatus, status);
        }
        
        // 按创建时间降序排序
        queryWrapper.lambda().orderByDesc(DoctorUpdateRequest::getCreateTime);
        
        IPage<DoctorUpdateRequest> result = doctorUpdateRequestService.page(page, queryWrapper);
        return ResultUtils.success("查询成功", result);
    }

    /**
     * 获取医生信息更新申请详情
     * @param requestId 申请ID
     * @return 申请详情
     */
    @GetMapping("/detail/{requestId}")
    public ResultVo getUpdateRequestDetail(@PathVariable Long requestId) {
        // 手动校验是否为管理员
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
        userQuery.lambda().eq(SysUser::getUsername, username);
        SysUser currentUser = userWebService.getOne(userQuery);
        if (currentUser == null || !"1".equals(currentUser.getIsAdmin())) {
            return ResultUtils.error("无权限访问,请联系管理员!");
        }
        DoctorUpdateRequest request = doctorUpdateRequestService.getById(requestId);
        if (request == null) {
            return ResultUtils.error("申请不存在");
        }
        return ResultUtils.success("查询成功", request);
    }

    /**
     * 审核医生信息更新申请
     * @param requestId 申请ID
     * @param status 审核状态：1-通过，2-拒绝
     * @param reviewComment 审核意见
     * @return 审核结果
     */
    @GetMapping("/review")
    public ResultVo reviewUpdateRequest(
            @RequestParam Long requestId,
            @RequestParam String status,
            @RequestParam(required = false) String reviewComment) {
        // 手动校验是否为管理员，并获取当前登录管理员ID（通过用户名查询）
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
        userQuery.lambda().eq(SysUser::getUsername, username);
        SysUser reviewer = userWebService.getOne(userQuery);
        if (reviewer == null || !"1".equals(reviewer.getIsAdmin())) {
            return ResultUtils.error("无权限访问,请联系管理员!");
        }
        Long reviewerId = reviewer.getUserId();
        
        boolean result = doctorUpdateRequestService.reviewUpdateRequest(requestId, status, reviewComment, reviewerId);
        
        if (result) {
            return ResultUtils.success("审核成功");
        } else {
            return ResultUtils.error("审核失败");
        }
    }

    /**
     * 医生查看自己的信息更新申请列表
     */
    @GetMapping("/my")
    public ResultVo getMyUpdateRequests(
            @RequestParam(defaultValue = "1") Long currentPage,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
        userQuery.lambda().eq(SysUser::getUsername, username);
        SysUser currentUser = userWebService.getOne(userQuery);
        if (currentUser == null) {
            return ResultUtils.error("用户不存在");
        }

        IPage<DoctorUpdateRequest> page = new Page<>(currentPage, pageSize);
        QueryWrapper<DoctorUpdateRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DoctorUpdateRequest::getDoctorId, currentUser.getUserId());
        if (status != null && !status.isEmpty()) {
            queryWrapper.lambda().eq(DoctorUpdateRequest::getStatus, status);
        }
        queryWrapper.lambda().orderByDesc(DoctorUpdateRequest::getCreateTime);

        IPage<DoctorUpdateRequest> result = doctorUpdateRequestService.page(page, queryWrapper);
        return ResultUtils.success("查询成功", result);
    }
}