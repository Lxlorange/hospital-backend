package com.itmk.netSystem.callAdd.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.netSystem.callAdd.entity.DoctorAddSlotRequest;
import com.itmk.netSystem.callAdd.service.DoctorAddSlotRequestService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addSlotRequest")
public class DoctorAddSlotRequestController {

    @Autowired
    private DoctorAddSlotRequestService addSlotRequestService;
    @Autowired
    private userWebService userWebService;

    /** 医生提交加号申请 */
    @PostMapping("/submit")
    @Transactional
    public ResultVo submit(@RequestBody DoctorAddSlotRequest request) {
        // 当前登录医生作为提交人
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
        userQuery.lambda().eq(SysUser::getUsername, username);
        SysUser currentUser = userWebService.getOne(userQuery);
        if (currentUser == null) {
            return ResultUtils.error("用户不存在");
        }
        request.setDoctorId(currentUser.getUserId().intValue());
        boolean ok = addSlotRequestService.submitAddSlotRequest(request);
        return ok ? ResultUtils.success("加号申请已提交，等待管理员审核") : ResultUtils.error("提交失败或当前仍有号源无需加号");
    }

    /** 管理员获取加号申请列表 */
    @GetMapping("/list")
    public ResultVo list(@RequestParam(defaultValue = "1") Long currentPage,
                         @RequestParam(defaultValue = "10") Long pageSize,
                         @RequestParam(required = false) String status) {
        if (!isAdmin()) {
            return ResultUtils.error("无权限访问,请联系管理员!");
        }
        IPage<DoctorAddSlotRequest> page = new Page<>(currentPage, pageSize);
        QueryWrapper<DoctorAddSlotRequest> query = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            query.lambda().eq(DoctorAddSlotRequest::getStatus, status);
        }
        query.lambda().orderByDesc(DoctorAddSlotRequest::getCreateTime);
        IPage<DoctorAddSlotRequest> result = addSlotRequestService.page(page, query);
        return ResultUtils.success("查询成功", result);
    }

    /** 管理员查看申请详情 */
    @GetMapping("/detail/{requestId}")
    public ResultVo detail(@PathVariable Long requestId) {
        if (!isAdmin()) {
            return ResultUtils.error("无权限访问,请联系管理员!");
        }
        DoctorAddSlotRequest req = addSlotRequestService.getById(requestId);
        if (req == null) {
            return ResultUtils.error("申请不存在");
        }
        return ResultUtils.success("查询成功", req);
    }

    /** 管理员审核 */
    @PostMapping("/review")
    @Transactional
    public ResultVo review(@RequestParam Long requestId,
                           @RequestParam String status,
                           @RequestParam(required = false) String reviewComment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
        userQuery.lambda().eq(SysUser::getUsername, username);
        SysUser reviewer = userWebService.getOne(userQuery);
        if (reviewer == null || !"1".equals(reviewer.getIsAdmin())) {
            return ResultUtils.error("无权限访问,请联系管理员!");
        }
        boolean ok = addSlotRequestService.reviewAddSlotRequest(requestId, status, reviewComment, reviewer.getUserId());
        return ok ? ResultUtils.success("审核完成") : ResultUtils.error("审核失败");
    }

    /** 医生查看自己的加号申请 */
    @GetMapping("/my")
    public ResultVo my(@RequestParam(defaultValue = "1") Long currentPage,
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
        IPage<DoctorAddSlotRequest> page = new Page<>(currentPage, pageSize);
        QueryWrapper<DoctorAddSlotRequest> query = new QueryWrapper<>();
        query.lambda().eq(DoctorAddSlotRequest::getDoctorId, currentUser.getUserId());
        if (status != null && !status.isEmpty()) {
            query.lambda().eq(DoctorAddSlotRequest::getStatus, status);
        }
        query.lambda().orderByDesc(DoctorAddSlotRequest::getCreateTime);
        IPage<DoctorAddSlotRequest> result = addSlotRequestService.page(page, query);
        return ResultUtils.success("查询成功", result);
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
        userQuery.lambda().eq(SysUser::getUsername, username);
        SysUser currentUser = userWebService.getOne(userQuery);
        return currentUser != null && "1".equals(currentUser.getIsAdmin());
    }
}