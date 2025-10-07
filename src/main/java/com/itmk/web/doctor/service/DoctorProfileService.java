package com.itmk.web.doctor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.doctor.entity.DoctorProfileVo;

/**
 * 医生个人主页服务接口
 */
public interface DoctorProfileService extends IService<DoctorProfileVo> {

    /**
     * 获取医生个人主页信息
     * @param doctorId 医生ID
     * @return 医生个人主页信息
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
}