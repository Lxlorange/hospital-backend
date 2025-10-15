package com.itmk.netSystem.doctor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.doctor.entity.DoctorUpdateRequest;

/**
 * 医生信息更新申请服务接口
 */
public interface DoctorUpdateRequestService extends IService<DoctorUpdateRequest> {
    
    /**
     * 提交医生信息更新申请
     * @param doctorUpdateRequest 更新申请信息
     * @return 是否提交成功
     */
    boolean submitUpdateRequest(DoctorUpdateRequest doctorUpdateRequest);
    
    /**
     * 审核医生信息更新申请
     * @param requestId 申请ID
     * @param status 审核状态：1-通过，2-拒绝
     * @param reviewComment 审核意见
     * @param reviewerId 审核人ID
     * @return 是否审核成功
     */
    boolean reviewUpdateRequest(Long requestId, String status, String reviewComment, Long reviewerId);
}