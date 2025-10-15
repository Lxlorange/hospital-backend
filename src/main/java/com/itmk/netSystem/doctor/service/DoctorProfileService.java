package com.itmk.netSystem.doctor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.doctor.entity.DoctorProfileVo;
import com.itmk.netSystem.userWeb.entity.SysUser;

import java.util.List;

/**
 * 医生个人主页服务接口
 */
public interface DoctorProfileService extends IService<DoctorProfileVo> {

    /**
     * 获取医生列表
     */
    DoctorProfileVo getDoctorProfile(Long doctorId);

    /**
     * 更新医生个人主页信息
     * @param doctorProfileVo 医生信息
     * @return 更新结果
     */
    boolean updateDoctorProfile(DoctorProfileVo doctorProfileVo);

    /**
     * 获取当前登录医生的个人主页信息
     * @return 医生个人主页信息
     */
    DoctorProfileVo getMyProfile();

    /**
     * 更新当前登录医生的个人主页信息
     * @param doctorProfileVo 医生信息
     * @return 更新结果
     */
    boolean updateMyProfile(DoctorProfileVo doctorProfileVo);
    
    /**
     * 提交医生个人主页信息更新申请
     * @param doctorProfileVo 医生信息
     * @return 提交结果
     */
    boolean submitUpdateRequest(DoctorProfileVo doctorProfileVo);
}