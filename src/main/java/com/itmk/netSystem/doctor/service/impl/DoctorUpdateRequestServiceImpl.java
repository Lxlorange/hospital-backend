package com.itmk.netSystem.doctor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.doctor.entity.DoctorProfileVo;
import com.itmk.netSystem.doctor.entity.DoctorUpdateRequest;
import com.itmk.netSystem.doctor.mapper.DoctorUpdateRequestMapper;
import com.itmk.netSystem.doctor.service.DoctorProfileService;
import com.itmk.netSystem.doctor.service.DoctorUpdateRequestService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 医生信息更新申请服务实现
 */
@Service
public class DoctorUpdateRequestServiceImpl extends ServiceImpl<DoctorUpdateRequestMapper, DoctorUpdateRequest> implements DoctorUpdateRequestService {

    @Autowired
    private userWebService sysUserService;
    
    @Autowired
    private DoctorProfileService doctorProfileService;

    @Override
    @Transactional
    public boolean submitUpdateRequest(DoctorUpdateRequest doctorUpdateRequest) {
        // 如果已存在该医生的待审核申请，则更新该申请内容，避免重复
        QueryWrapper<DoctorUpdateRequest> pendingQuery = new QueryWrapper<>();
        pendingQuery.lambda()
                .eq(DoctorUpdateRequest::getDoctorId, doctorUpdateRequest.getDoctorId())
                .eq(DoctorUpdateRequest::getStatus, "0");

        DoctorUpdateRequest existingPending = this.getOne(pendingQuery);
        Date now = new Date();

        if (existingPending != null) {
            existingPending.setIntroduction(doctorUpdateRequest.getIntroduction());
            existingPending.setVisitAddress(doctorUpdateRequest.getVisitAddress());
            existingPending.setGoodAt(doctorUpdateRequest.getGoodAt());
            existingPending.setPrice(doctorUpdateRequest.getPrice());
            existingPending.setUpdateTime(now);
            return this.updateById(existingPending);
        }

        // 否则创建新的待审核申请
        doctorUpdateRequest.setStatus("0");
        doctorUpdateRequest.setCreateTime(now);
        doctorUpdateRequest.setUpdateTime(now);
        return this.save(doctorUpdateRequest);
    }

    @Override
    @Transactional
    public boolean reviewUpdateRequest(Long requestId, String status, String reviewComment, Long reviewerId) {
        // 查询申请记录
        DoctorUpdateRequest request = this.getById(requestId);
        if (request == null) {
            return false;
        }
        
        // 更新申请状态
        request.setStatus(status);
        request.setReviewComment(reviewComment);
        request.setReviewerId(reviewerId);
        request.setReviewTime(new Date());
        request.setUpdateTime(new Date());
        
        boolean updated = this.updateById(request);
        
        // 如果审核通过，则更新医生信息
        if (updated && "1".equals(status)) {
            SysUser sysUser = new SysUser();
            sysUser.setUserId(request.getDoctorId());
            sysUser.setIntroduction(request.getIntroduction());
            sysUser.setVisitAddress(request.getVisitAddress());
            sysUser.setGoodAt(request.getGoodAt());
            sysUser.setPrice(request.getPrice());
            sysUser.setUpdateTime(new Date());
            
            return sysUserService.updateById(sysUser);
        }
        
        return updated;
    }
}