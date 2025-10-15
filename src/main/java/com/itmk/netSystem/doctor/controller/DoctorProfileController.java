package com.itmk.netSystem.doctor.controller;

import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.netSystem.doctor.entity.DoctorProfileVo;
import com.itmk.netSystem.doctor.service.DoctorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 医生个人主页控制器
 */
@RestController
@RequestMapping("/api/doctorProfile")
public class DoctorProfileController {

    @Autowired
    private DoctorProfileService doctorProfileService;

    /**
     * 获取医生个人主页信息
     * @param doctorId 医生ID
     * @return 医生个人主页信息
     */
    @GetMapping("/getDoctorProfile")
    public ResultVo getDoctorProfile(@RequestParam Long doctorId) {
        DoctorProfileVo doctorProfile = doctorProfileService.getDoctorProfile(doctorId);
        return ResultUtils.success("查询成功", doctorProfile);
    }

    /**
     * 更新医生个人主页信息
     * @param doctorProfileVo 医生信息
     * @return 更新结果
     */
    //@PreAuthorize("hasAuthority('sys:doctor:profile:edit')")
    @PostMapping("/updateDoctorProfile")
    public ResultVo updateDoctorProfile(@RequestBody DoctorProfileVo doctorProfileVo) {
        boolean result = doctorProfileService.updateDoctorProfile(doctorProfileVo);
        if (result) {
            return ResultUtils.success("更新成功!");
        }
        return ResultUtils.error("更新失败!");
    }

    /**
     * 获取当前登录医生的个人主页信息
     * @return 医生个人主页信息
     */
    @GetMapping("/getMyProfile")
    public ResultVo getMyProfile() {
        DoctorProfileVo doctorProfile = doctorProfileService.getMyProfile();
        return ResultUtils.success("查询成功", doctorProfile);
    }

    /**
     * 更新当前登录医生的个人主页信息（提交审核申请）
     * @param doctorProfileVo 医生信息
     * @return 提交结果
     */
    //@PreAuthorize("hasAuthority('sys:doctor:profile:edit')")
    @PostMapping("/updateMyProfile")
    public ResultVo updateMyProfile(@RequestBody DoctorProfileVo doctorProfileVo) {
        boolean result = doctorProfileService.submitUpdateRequest(doctorProfileVo);
        if (result) {
            return ResultUtils.success("更新申请已提交，等待管理员审核");
        }
        return ResultUtils.error("提交申请失败");
    }
}