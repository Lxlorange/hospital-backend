package com.itmk.web.make_order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.make_order.entity.MakeOrder;

 
public interface MakeOrderService extends IService<MakeOrder> {
    void callVisit(MakeOrder makeOrder);
}
