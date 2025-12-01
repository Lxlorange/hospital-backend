package com.itmk.netSystem.callAdd.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itmk.netSystem.callAdd.entity.DoctorAddSlotRequest;
import com.itmk.netSystem.callAdd.service.DoctorAddSlotRequestService;
import com.itmk.netSystem.treatpatient.entity.VisitUser;
import com.itmk.netSystem.treatpatient.service.TreatPatientService;
import com.itmk.netSystem.userPatientPhone.entity.WxUser;
import com.itmk.netSystem.userPatientPhone.service.UserPatientPhoneService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/addSlotRequest")
public class DoctorAddSlotRequestController {

    @Autowired
    private DoctorAddSlotRequestService addSlotRequestService;
    @Autowired
    private userWebService userWebService;
    @Autowired
    private TreatPatientService treatPatientService;
    @Autowired
    private UserPatientPhoneService userPatientPhoneService;

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
        return ok ? ResultUtils.success("加号成功，已为患者创建预约") : ResultUtils.error("加号失败：当前仍有号源无需加号或该患者已有预约");
    }

    // 以下方法已注释，因为加号功能改为直接创建订单，不再需要审核流程
    
    /*
    /** 管理员获取加号申请列表 */
    /*
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
    /*
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
    /*
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
    /*
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
    */

    /**
     * 为加号选择患者：支持按手机号/身份证号/姓名查询，返回 userId 及其名下的 visitUserId 列表
     * 查询优先级：phone > idCard > name
     */
    @GetMapping("/patientOptions")
    public ResultVo patientOptions(@RequestParam(required = false) String phone,
                                   @RequestParam(required = false) String idCard,
                                   @RequestParam(required = false) String name) {
        if ((phone == null || phone.isEmpty()) && (idCard == null || idCard.isEmpty()) && (name == null || name.isEmpty())) {
            return ResultUtils.error("请至少提供一个查询条件: phone / idCard / name");
        }

        // 结果结构：每个用户对应一个候选项，包含其名下的就诊人
        List<Map<String, Object>> result = new ArrayList<>();

        // 1) 按手机号查询用户及其就诊人
        if (phone != null && !phone.isEmpty()) {
            WxUser user = userPatientPhoneService.findByPhone(phone);
            if (user != null) {
                QueryWrapper<VisitUser> q = new QueryWrapper<>();
                q.lambda().eq(VisitUser::getUserId, user.getUserId());
                List<VisitUser> visits = treatPatientService.list(q);
                result.add(buildUserOption(user, visits));
            }
            return wrapPatientOptions(result);
        }

        // 2) 按身份证号查询就诊人及其对应用户
        if (idCard != null && !idCard.isEmpty()) {
            VisitUser vu = treatPatientService.findPatientByIdCard(idCard);
            if (vu != null) {
                WxUser user = userPatientPhoneService.getById(vu.getUserId());
                if (user != null) {
                    QueryWrapper<VisitUser> q = new QueryWrapper<>();
                    q.lambda().eq(VisitUser::getUserId, user.getUserId());
                    List<VisitUser> visits = treatPatientService.list(q);
                    result.add(buildUserOption(user, visits));
                }
            }
            return wrapPatientOptions(result);
        }

        // 3) 按姓名模糊查询就诊人，按 userId 分组，汇总名下就诊人
        List<VisitUser> byName = treatPatientService.searchPatientsByName(name);
        Map<Integer, List<VisitUser>> grouped = byName.stream().collect(Collectors.groupingBy(VisitUser::getUserId));
        for (Map.Entry<Integer, List<VisitUser>> entry : grouped.entrySet()) {
            WxUser user = userPatientPhoneService.getById(entry.getKey());
            if (user != null) {
                result.add(buildUserOption(user, entry.getValue()));
            }
        }
        return wrapPatientOptions(result);
    }

    private Map<String, Object> buildUserOption(WxUser user, List<VisitUser> visits) {
        Map<String, Object> item = new HashMap<>();
        item.put("userId", user.getUserId());
        item.put("userDisplayName", user.getDisplayName());
        item.put("userMaskedPhone", user.getMaskedPhone());
        List<Map<String, Object>> visitOpts = new ArrayList<>();
        for (VisitUser v : visits) {
            Map<String, Object> vo = new HashMap<>();
            vo.put("visitUserId", v.getVisitId());
            vo.put("visitname", v.getVisitname());
            vo.put("maskedIdCard", v.getMaskedIdCard());
            vo.put("maskedPhone", v.getMaskedPhone());
            visitOpts.add(vo);
        }
        item.put("visitOptions", visitOpts);
        return item;
    }

    private ResultVo wrapPatientOptions(List<Map<String, Object>> list) {
        if (list.isEmpty()) {
            return ResultUtils.error("未找到匹配的患者信息");
        }
        return ResultUtils.success("查询成功", list);
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