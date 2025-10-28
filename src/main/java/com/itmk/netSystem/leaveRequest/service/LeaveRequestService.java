package com.itmk.netSystem.leaveRequest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.leaveRequest.entity.LeaveRequest;

/**
 * 请假申请服务接口
 */
public interface LeaveRequestService extends IService<LeaveRequest> {
    /**
     * 新增请假申请，默认状态为“0=待审核”
     */
    boolean submit(LeaveRequest request);

    /**
     * 审批请假申请
     * @param requestId 申请ID
     * @param status 审批状态：1=通过，2=拒绝
     * @param reviewComment 审批意见
     * @param reviewerId 审批人ID（可为空）
     */
    boolean approve(Long requestId, String status, String reviewComment, Long reviewerId);
}