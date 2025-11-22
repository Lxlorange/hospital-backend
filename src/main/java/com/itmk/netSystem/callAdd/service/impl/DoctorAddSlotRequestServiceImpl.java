package com.itmk.netSystem.callAdd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.call.entity.MakeOrder;
import com.itmk.netSystem.call.service.CallService;
import com.itmk.netSystem.callAdd.entity.DoctorAddSlotRequest;
import com.itmk.netSystem.callAdd.mapper.DoctorAddSlotRequestMapper;
import com.itmk.netSystem.callAdd.service.DoctorAddSlotRequestService;
import com.itmk.netSystem.schedule.service.ScheduleService;
import com.itmk.netSystem.userPatientPhone.entity.WxUser;
import com.itmk.netSystem.userPatientPhone.service.UserPatientPhoneService;
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
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private UserPatientPhoneService userPatientPhoneService;

    @Override
    @Transactional
    public boolean submitAddSlotRequest(DoctorAddSlotRequest request) {
        // 检查排班是否存在且已满
        System.out.println(request.getScheduleId());
        ScheduleDetail schedule = setWorkService.selectByWorkId(request.getScheduleId()).get(0);
        if (schedule == null) {
            return false;
        }

        
        // 防重复：同用户同排班已存在"已预约"订单则拒绝创建
        QueryWrapper<MakeOrder> dup = new QueryWrapper<>();
        dup.lambda()
                .eq(MakeOrder::getUserId, request.getUserId())
                .eq(MakeOrder::getScheduleId, request.getScheduleId())
                .eq(MakeOrder::getStatus, "1");
        if (callService.count(dup) > 0) {
            return false; // 已存在预约订单，不允许重复加号
        }
        // 直接创建预约订单（加号不占用 lastAmount）
        MakeOrder order = new MakeOrder();
        order.setScheduleId(request.getScheduleId());
        order.setUserId(request.getUserId());
        order.setVisitUserId(request.getVisitUserId());
        order.setDoctorId(request.getDoctorId());
        order.setTimes(schedule.getTimes() == null ? null : schedule.getTimes().toString());
        order.setTimesArea(schedule.getTimeSlot() == null ? null : (schedule.getTimeSlot() == 0 ? "0" : "1"));
        order.setWeek(schedule.getWeek());
        order.setCreateTime(new Date());
        java.math.BigDecimal originalPrice = schedule.getPrice() == null ? java.math.BigDecimal.ZERO : schedule.getPrice();
        java.math.BigDecimal payPrice = originalPrice;
        try {
            WxUser wxUser = userPatientPhoneService.getById(request.getUserId());
            String identity = wxUser != null ? wxUser.getIdentityStatus() : null;
            if (identity != null) {
                String s = identity.trim();
                if ("学生".equals(s)) {
                    payPrice = originalPrice.multiply(new java.math.BigDecimal("0.05"));
                } else if ("教师".equals(s)) {
                    payPrice = originalPrice.multiply(new java.math.BigDecimal("0.10"));
                }
            }
        } catch (Exception ignored) {}
        order.setPrice(payPrice.setScale(2, java.math.RoundingMode.HALF_UP));
        order.setAddress(request.getAddress());
        order.setStatus("1"); // 已预约
        order.setHasVisit("0");
        order.setHasCall("0");
        if (schedule.getLastAmount() != null && schedule.getLastAmount() > 0) {
            schedule.setLastAmount(schedule.getLastAmount()-1);
            setWorkService.saveOrUpdate(schedule);
        }
        return callService.save(order);
    }

    // 审核方法已注释，因为加号功能改为直接创建订单，不再需要审核流程
    /*
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
            // 防重复：同用户同排班已存在"已预约"订单则拒绝创建
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
    */
}