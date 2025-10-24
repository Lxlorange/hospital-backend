package com.itmk.netSystem.callAdd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.call.entity.MakeOrder;
import com.itmk.netSystem.call.service.CallService;
import com.itmk.netSystem.callAdd.entity.DoctorAddSlotRequest;
import com.itmk.netSystem.callAdd.mapper.DoctorAddSlotRequestMapper;
import com.itmk.netSystem.callAdd.service.DoctorAddSlotRequestService;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.service.setWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class DoctorAddSlotRequestServiceImpl extends ServiceImpl<DoctorAddSlotRequestMapper, DoctorAddSlotRequest> implements DoctorAddSlotRequestService {

    @Autowired
    private setWorkService setWorkService;
    @Autowired
    private CallService callService;

    @Override
    @Transactional
    public boolean submitAddSlotRequest(DoctorAddSlotRequest request) {
        // 检查排班是否存在且已满
        ScheduleDetail schedule = setWorkService.getById(request.getScheduleId());
        if (schedule == null) {
            return false;
        }
        if (schedule.getLastAmount() != null && schedule.getLastAmount() > 0) {
            // 仍有号源无需加号
            return false;
        }
        // 避免重复提交：同一医生同一排班同一就诊人若有“待审核”记录则更新
        QueryWrapper<DoctorAddSlotRequest> pendingQuery = new QueryWrapper<>();
        pendingQuery.lambda()
                .eq(DoctorAddSlotRequest::getDoctorId, request.getDoctorId())
                .eq(DoctorAddSlotRequest::getScheduleId, request.getScheduleId())
                .eq(DoctorAddSlotRequest::getVisitUserId, request.getVisitUserId())
                .eq(DoctorAddSlotRequest::getStatus, "0");
        DoctorAddSlotRequest existing = this.getOne(pendingQuery);
        Date now = new Date();
        if (existing != null) {
            existing.setReason(request.getReason());
            existing.setUpdateTime(now);
            return this.updateById(existing);
        }
        request.setStatus("0");
        request.setCreateTime(now);
        request.setUpdateTime(now);
        // 从排班复制必要信息
        request.setPrice(schedule.getPrice());
        request.setWeek(schedule.getWeek());
        request.setTimes(schedule.getTimes() == null ? null : schedule.getTimes().toString());
        request.setTimesArea(schedule.getTimeSlot() == null ? null : (schedule.getTimeSlot() == 0 ? "0" : "1"));
        return this.save(request);
    }

    @Override
    @Transactional
    public boolean reviewAddSlotRequest(Long requestId, String status, String reviewComment, Long reviewerId) {
        DoctorAddSlotRequest req = this.getById(requestId);
        if (req == null) {
            return false;
        }
        req.setStatus(status);
        req.setReviewComment(reviewComment);
        req.setReviewerId(reviewerId);
        req.setReviewTime(new Date());
        req.setUpdateTime(new Date());
        boolean updated = this.updateById(req);
        if (!updated) {
            return false;
        }
        // 审核通过则创建预约订单（不占用 lastAmount）
        if ("1".equals(status)) {
            ScheduleDetail schedule = setWorkService.getById(req.getScheduleId());
            if (schedule == null) {
                return false;
            }
            // 防重复：同用户同排班已存在“已预约”订单则拒绝创建
            QueryWrapper<MakeOrder> dup = new QueryWrapper<>();
            dup.lambda()
                    .eq(MakeOrder::getUserId, req.getUserId())
                    .eq(MakeOrder::getScheduleId, req.getScheduleId())
                    .eq(MakeOrder::getStatus, "1");
            if (callService.count(dup) > 0) {
                return true; // 审核记录更新成功即可，订单已存在不再重复创建
            }
            MakeOrder order = new MakeOrder();
            order.setScheduleId(req.getScheduleId());
            order.setUserId(req.getUserId());
            order.setVisitUserId(req.getVisitUserId());
            order.setDoctorId(req.getDoctorId());
            order.setTimes(req.getTimes());
            order.setTimesArea(req.getTimesArea());
            order.setWeek(req.getWeek());
            order.setCreateTime(new Date());
            order.setPrice(req.getPrice());
            order.setAddress(req.getAddress());
            order.setStatus("1");
            order.setHasVisit("0");
            order.setHasCall("0");
            return callService.save(order);
        }
        return true;
    }
}