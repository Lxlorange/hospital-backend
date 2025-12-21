package com.itmk.netSystem.call.service.implemenet;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.call.entity.MakeOrder;
import com.itmk.netSystem.call.mapper.CallMapper;
import com.itmk.netSystem.call.service.CallService;
import com.itmk.netSystem.see.entity.MakeOrderVisit;
import com.itmk.netSystem.see.service.SeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
public class CallServiceImplement extends ServiceImpl<CallMapper, MakeOrder> implements CallService {
   @Autowired
   private SeeService seeService;
   @Autowired
   private com.itmk.netSystem.setWork.service.setWorkService setWorkService;
    @Override
    public MakeOrder getMakeOrderDetail(Integer makeId) {
        return this.baseMapper.selectById(makeId);
    }

    @Override
    @Transactional
    public boolean cancelAppointment(Integer makeId) {
        MakeOrder update = new MakeOrder();
        update.setMakeId(makeId);
        update.setStatus("2");
        return this.baseMapper.updateById(update) > 0;
    }

    @Override
    public boolean updateVisitStatus(Integer makeId, String hasVisitStatus) {
        MakeOrder update = new MakeOrder();
        update.setMakeId(makeId);
        update.setHasVisit(hasVisitStatus);
        return this.baseMapper.updateById(update) > 0;
    }

    @Override
    public List<MakeOrder> listPendingAppointmentsByDoctor(Integer doctorId) {
        QueryWrapper<MakeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(MakeOrder::getDoctorId, doctorId)
                .eq(MakeOrder::getHasVisit, "0")
                .eq(MakeOrder::getStatus, "1")
                .orderByAsc(MakeOrder::getTimes);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public IPage<MakeOrder> getAppointmentHistoryByUserId(IPage<MakeOrder> page, Integer userId) {
        QueryWrapper<MakeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MakeOrder::getUserId, userId).orderByDesc(MakeOrder::getCreateTime);
        return this.baseMapper.selectPage(page, queryWrapper);
    }
    @Override
    @Transactional
    public void callVisit(MakeOrder makeOrder) {
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<MakeOrder> uw = new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        uw.lambda()
                .eq(MakeOrder::getMakeId, makeOrder.getMakeId())
                .eq(MakeOrder::getHasCall, "0")
                .set(MakeOrder::getHasCall, "1")
                .set(MakeOrder::getCalledTime, new Date())
                .set(MakeOrder::getMissed, "0")
                .set(MakeOrder::getStatus, "1");
        this.baseMapper.update(null, uw);

        QueryWrapper<MakeOrderVisit> query = new QueryWrapper<>();
        query.lambda().eq(MakeOrderVisit::getMakeId,makeOrder.getMakeId())
                .eq(MakeOrderVisit::getVisitUserId,makeOrder.getVisitUserId())
                .eq(MakeOrderVisit::getUserId,makeOrder.getUserId());
        MakeOrderVisit one = seeService.getOne(query);
        if(one == null){
            MakeOrderVisit visit = new MakeOrderVisit();
            BeanUtils.copyProperties(makeOrder,visit);
            visit.setCreateTime(new Date());
            visit.setHasVisit("0");
            visit.setHasLive("0");
            seeService.save(visit);
        }

    }

    @Override
    @Transactional
    public boolean checkIn(Integer makeId) {
        MakeOrder order = this.baseMapper.selectById(makeId);
        if (order == null) {
            return false;
        }
        if (!"1".equals(order.getStatus())) {
            return false;
        }
        if ("1".equals(order.getHasVisit())) {
            return false;
        }
        java.time.LocalDate today = java.time.LocalDate.now();
        /*
        try {
            java.time.LocalDate appointmentDate = java.time.LocalDate.parse(order.getTimes(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (!today.equals(appointmentDate)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }*/
        Integer scheduleId = order.getScheduleId();
        if (scheduleId == null) {
            return false;
        }
        com.itmk.netSystem.setWork.entity.ScheduleDetail schedule = null;
        try {
            java.util.List<com.itmk.netSystem.setWork.entity.ScheduleDetail> list = setWorkService.selectByWorkId(scheduleId);
            if (list != null && !list.isEmpty()) {
                schedule = list.get(0);
            }
        } catch (Exception ignored) {}
        if (schedule == null) {
            return false;
        }
        Integer timeSlot = schedule.getTimeSlot();
        java.time.LocalTime now = java.time.LocalTime.now();
        boolean inSlot;
        if (timeSlot != null && timeSlot == 0) {
            inSlot = !now.isAfter(java.time.LocalTime.NOON);
        } else if (timeSlot != null && timeSlot == 1) {
            inSlot = !now.isBefore(java.time.LocalTime.NOON);
        } else {
            inSlot = true;
        }
        if (!inSlot) {
            return false;
        }
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<MakeOrder> uw = new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        uw.lambda()
                .eq(MakeOrder::getMakeId, makeId)
                .set(MakeOrder::getSignInStatus, "1")
                .set(MakeOrder::getSignInTime, new Date())
                .set(MakeOrder::getMissed, "0");
        return this.baseMapper.update(null, uw) > 0;
    }

    public java.util.List<MakeOrder> listScheduleQueue(Integer scheduleId) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MakeOrder> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.lambda()
                .eq(MakeOrder::getScheduleId, scheduleId)
                .eq(MakeOrder::getStatus, "1")
                .eq(MakeOrder::getHasVisit, "0")
                .eq(MakeOrder::getSignInStatus, "1")
                .orderByAsc(MakeOrder::getSignInTime)
                .orderByAsc(MakeOrder::getCreateTime)
                .orderByAsc(MakeOrder::getMakeId);
        return this.baseMapper.selectList(qw);
    }

    @Transactional
    public MakeOrder callNext(Integer scheduleId) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MakeOrder> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.lambda()
                .eq(MakeOrder::getScheduleId, scheduleId)
                .eq(MakeOrder::getStatus, "1")
                .eq(MakeOrder::getHasVisit, "0")
                .eq(MakeOrder::getSignInStatus, "1")
                .eq(MakeOrder::getHasCall, "0")
                .orderByAsc(MakeOrder::getSignInTime)
                .orderByAsc(MakeOrder::getCreateTime)
                .orderByAsc(MakeOrder::getMakeId)
                .last("limit 1");
        MakeOrder next = this.baseMapper.selectOne(qw);
        if (next == null) {
            return null;
        }
        callVisit(next);
        return this.baseMapper.selectById(next.getMakeId());
    }

    @Override
    public java.util.Map<String, Object> getQueueStatus(Integer makeId) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        MakeOrder order = this.baseMapper.selectById(makeId);
        if (order == null) {
            result.put("aheadCount", 0);
            result.put("position", 0);
            result.put("totalUncalled", 0);
            result.put("totalQueued", 0);
            result.put("inQueue", false);
            return result;
        }
        Integer scheduleId = order.getScheduleId();
        if (scheduleId == null) {
            result.put("aheadCount", 0);
            result.put("position", 0);
            result.put("totalUncalled", 0);
            result.put("totalQueued", 0);
            result.put("inQueue", false);
            return result;
        }
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MakeOrder> uncalledQ = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        uncalledQ.lambda()
                .eq(MakeOrder::getScheduleId, scheduleId)
                .eq(MakeOrder::getStatus, "1")
                .eq(MakeOrder::getHasVisit, "0")
                .eq(MakeOrder::getSignInStatus, "1")
                .eq(MakeOrder::getHasCall, "0")
                .orderByAsc(MakeOrder::getSignInTime)
                .orderByAsc(MakeOrder::getCreateTime)
                .orderByAsc(MakeOrder::getMakeId);
        java.util.List<MakeOrder> uncalledList = this.baseMapper.selectList(uncalledQ);
        int totalUncalled = uncalledList.size();
        int totalQueued = listScheduleQueue(scheduleId).size();

        boolean signedIn = "1".equals(order.getSignInStatus());
        boolean called = "1".equals(order.getHasCall());

        int ahead = 0;
        int position = 0;
        if (!signedIn) {
            ahead = totalUncalled;
            position = totalUncalled + 1;
        } else if (called) {
            ahead = 0;
            position = 0;
        } else {
            for (int i = 0; i < uncalledList.size(); i++) {
                MakeOrder m = uncalledList.get(i);
                if (java.util.Objects.equals(m.getMakeId(), order.getMakeId())) {
                    ahead = i;
                    position = i + 1;
                    break;
                }
            }
        }
        result.put("aheadCount", ahead);
        result.put("position", position);
        result.put("totalUncalled", totalUncalled);
        result.put("totalQueued", totalQueued);
        result.put("inQueue", signedIn && !called);
        result.put("hasCall", order.getHasCall());
        result.put("signInStatus", order.getSignInStatus());
        result.put("scheduleId", scheduleId);
        return result;
    }
}
