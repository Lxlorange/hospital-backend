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
        MakeOrder update = new MakeOrder();
        update.setMakeId(makeId);
        update.setSignInStatus("1");
        update.setSignInTime(new Date());
        update.setMissed("0");
        return this.baseMapper.updateById(update) > 0;
    }
}
