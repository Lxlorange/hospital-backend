package com.itmk.netSystem.doctor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.doctor.entity.DoctorProfileVo;
import com.itmk.netSystem.doctor.mapper.DoctorProfileMapper;
import com.itmk.netSystem.doctor.service.DoctorProfileService;
import com.itmk.netSystem.doctor.service.DoctorUpdateRequestService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itmk.netSystem.doctor.entity.DoctorUpdateRequest;
/**
 * 医生个人主页服务实现
 */
@Service
public class DoctorProfileServiceImpl extends ServiceImpl<DoctorProfileMapper, DoctorProfileVo> implements DoctorProfileService {

    @Autowired
    private userWebService userWebService;

    @Override
    public DoctorProfileVo getDoctorProfile(Long doctorId) {
        MPJLambdaWrapper<SysUser> query = new MPJLambdaWrapper<>();
        query.selectAll(SysUser.class)
                .select(Department::getDeptName)
                .leftJoin(Department.class, Department::getDeptId, SysUser::getDeptId)
                .eq(SysUser::getUserId, doctorId);

        SysUser sysUser = userWebService.getOne(query);
        if (sysUser == null) {
            return null;
        }

        DoctorProfileVo doctorProfileVo = new DoctorProfileVo();
        BeanUtils.copyProperties(sysUser, doctorProfileVo);
        return doctorProfileVo;
    }

    @Override
    @Transactional
    public boolean updateDoctorProfile(DoctorProfileVo doctorProfileVo) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(doctorProfileVo, sysUser);
        return userWebService.updateById(sysUser);
    }

    @Override
    public DoctorProfileVo getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getUsername, username);
        SysUser sysUser = userWebService.getOne(query);
        
        if (sysUser == null) {
            return null;
        }
        
        return getDoctorProfile(sysUser.getUserId());
    }

    @Override
    @Transactional
    public boolean updateMyProfile(DoctorProfileVo doctorProfileVo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getUsername, username);
        SysUser currentUser = userWebService.getOne(query);
        
        if (currentUser == null) {
            return false;
        }
        if(doctorProfileVo.getUserId() == 0){
            doctorProfileVo.setUserId(currentUser.getUserId());
        }
        // 只能更新自己的信息
        if (!currentUser.getUserId().equals(doctorProfileVo.getUserId())) {
            return false;
        }

        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(doctorProfileVo, sysUser);
        return userWebService.updateById(sysUser);
    }
    
    @Autowired
    private DoctorUpdateRequestService doctorUpdateRequestService;
    
    @Override
    @Transactional
    public boolean submitUpdateRequest(DoctorProfileVo doctorProfileVo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getUsername, username);
        SysUser currentUser = userWebService.getOne(query);
        
        if (currentUser == null) {
            return false;
        }
        
        // 创建更新申请
        DoctorUpdateRequest request = new DoctorUpdateRequest();
        request.setDoctorId(currentUser.getUserId());
        request.setUsername(currentUser.getUsername());
        request.setNickName(currentUser.getNickName());
        request.setIntroduction(doctorProfileVo.getIntroduction());
        request.setVisitAddress(doctorProfileVo.getVisitAddress());
        request.setGoodAt(doctorProfileVo.getGoodAt());
        request.setPrice(doctorProfileVo.getPrice());
        
        return doctorUpdateRequestService.submitUpdateRequest(request);
    }
}