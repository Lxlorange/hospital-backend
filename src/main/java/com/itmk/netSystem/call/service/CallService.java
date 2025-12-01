package com.itmk.netSystem.call.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.call.entity.MakeOrder;

import java.util.List;
 
public interface CallService extends IService<MakeOrder> {
    MakeOrder getMakeOrderDetail(Integer makeId);

    boolean cancelAppointment(Integer makeId);

    boolean updateVisitStatus(Integer makeId, String hasVisitStatus);

    List<MakeOrder> listPendingAppointmentsByDoctor(Integer doctorId);

    IPage<MakeOrder> getAppointmentHistoryByUserId(IPage<MakeOrder> page, Integer userId);
    void callVisit(MakeOrder makeOrder);
}
