package com.itmk.netSystem.callAdd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.callAdd.entity.DoctorAddSlotRequest;

public interface DoctorAddSlotRequestService extends IService<DoctorAddSlotRequest> {
    boolean submitAddSlotRequest(DoctorAddSlotRequest request);
}