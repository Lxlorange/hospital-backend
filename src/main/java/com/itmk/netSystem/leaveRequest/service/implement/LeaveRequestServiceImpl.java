package com.itmk.netSystem.leaveRequest.service.implement;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.leaveRequest.entity.LeaveRequest;
import com.itmk.netSystem.leaveRequest.mapper.LeaveRequestMapper;
import com.itmk.netSystem.leaveRequest.service.LeaveRequestService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LeaveRequestServiceImpl extends ServiceImpl<LeaveRequestMapper, LeaveRequest> implements LeaveRequestService {

    @Override
    public boolean submit(LeaveRequest request) {
        Date now = new Date();
        request.setStatus("0");
        request.setCreateTime(now);
        request.setUpdateTime(now);
        return this.save(request);
    }

    @Override
    public boolean approve(Long requestId, String status, String reviewComment, Long reviewerId) {
        LeaveRequest update = new LeaveRequest();
        update.setRequestId(requestId);
        update.setStatus(status);
        update.setReviewComment(reviewComment);
        update.setReviewerId(reviewerId);
        update.setReviewTime(new Date());
        update.setUpdateTime(new Date());
        return this.updateById(update);
    }
}