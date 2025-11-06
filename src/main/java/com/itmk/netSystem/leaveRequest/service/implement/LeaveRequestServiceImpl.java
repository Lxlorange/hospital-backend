package com.itmk.netSystem.leaveRequest.service.implement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.call.entity.MakeOrder;
import com.itmk.netSystem.call.service.CallService;
import com.itmk.netSystem.leaveRequest.entity.LeaveRequest;
import com.itmk.netSystem.leaveRequest.mapper.LeaveRequestMapper;
import com.itmk.netSystem.leaveRequest.service.LeaveRequestService;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.service.setWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class LeaveRequestServiceImpl extends ServiceImpl<LeaveRequestMapper, LeaveRequest> implements LeaveRequestService {

    @Autowired
    private setWorkService setWorkService;

    @Autowired
    private CallService callService;

    @Override
    public boolean submit(LeaveRequest request) {
        Date now = new Date();
        request.setStatus("0");
        request.setCreateTime(now);
        request.setUpdateTime(now);
        return this.save(request);
    }

    @Override
    @Transactional
    public boolean approve(Long requestId, String status, String reviewComment, Long reviewerId) {
        // 1) 更新请假申请本身
        LeaveRequest update = new LeaveRequest();
        update.setRequestId(requestId);
        update.setStatus(status);
        update.setReviewComment(reviewComment);
        update.setReviewerId(reviewerId);
        update.setReviewTime(new Date());
        update.setUpdateTime(new Date());
        boolean ok = this.updateById(update);
        if (!ok) {
            return false;
        }

        // 2) 审批通过时，停诊对应排班 & 将挂号改为待改签
        if ("1".equals(status)) {
            LeaveRequest req = this.getById(requestId);
            if (req == null || req.getScheduleId() == null) {
                return false;
            }

            // 2.1 更新排班类型为 0（停诊）
            ScheduleDetail detail = new ScheduleDetail();
            detail.setScheduleId(req.getScheduleId().intValue());
            detail.setType("0");
            setWorkService.updateById(detail);

            // 2.2 将该排班下所有状态为“已预约(1)”的订单改为“待改签(3)”
            QueryWrapper<MakeOrder> orderQuery = new QueryWrapper<>();
            orderQuery.lambda()
                    .eq(MakeOrder::getScheduleId, req.getScheduleId())
                    .eq(MakeOrder::getStatus, "1");
            // 批量更新：逐条读取后更新状态为3
            for (MakeOrder order : callService.list(orderQuery)) {
                MakeOrder toUpdate = new MakeOrder();
                toUpdate.setMakeId(order.getMakeId());
                toUpdate.setStatus("3"); // 待改签(reschedule)
                callService.updateById(toUpdate);
            }
        }

        return true;
    }
}