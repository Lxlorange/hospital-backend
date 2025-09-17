package com.itmk.web.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.order.entity.MakeOrder;

 
public interface MakeOrderService extends IService<MakeOrder> {
    void callVisit(MakeOrder makeOrder);
}
