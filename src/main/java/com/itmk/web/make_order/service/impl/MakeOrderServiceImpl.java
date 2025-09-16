package com.itmk.web.make_order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.make_order.entity.MakeOrder;
import com.itmk.web.make_order.mapper.MakeOrderMapper;
import com.itmk.web.make_order.service.MakeOrderService;
import com.itmk.web.make_order_visit.entity.MakeOrderVisit;
import com.itmk.web.make_order_visit.service.MakeOrderVisitService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

 
@Service
public class MakeOrderServiceImpl extends ServiceImpl<MakeOrderMapper, MakeOrder> implements MakeOrderService {

   @Autowired
   private MakeOrderVisitService makeOrderVisitService;
    @Override
    @Transactional
    public void callVisit(MakeOrder makeOrder) {
        //更新叫号的状态
        makeOrder.setHasCall("1");
        this.baseMapper.updateById(makeOrder);
        //就诊表新增一条数据,先查询是否已经增加数据
        QueryWrapper<MakeOrderVisit> query = new QueryWrapper<>();
        query.lambda().eq(MakeOrderVisit::getMakeId,makeOrder.getMakeId())
                .eq(MakeOrderVisit::getVisitUserId,makeOrder.getVisitUserId())
                .eq(MakeOrderVisit::getUserId,makeOrder.getUserId());
        MakeOrderVisit one = makeOrderVisitService.getOne(query);
        if(one == null){
            MakeOrderVisit visit = new MakeOrderVisit();
            BeanUtils.copyProperties(makeOrder,visit);
            visit.setCreateTime(new Date());
            visit.setHasVisit("0");
            visit.setHasLive("0");
            makeOrderVisitService.save(visit);
        }

    }
}
