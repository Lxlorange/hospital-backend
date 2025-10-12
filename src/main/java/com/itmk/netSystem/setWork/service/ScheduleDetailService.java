package com.itmk.netSystem.setWork.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.entity.ScheduleDetailPageParm;
//import com.itmk.web.phone.entity.DoctorDetailParm;


public interface ScheduleDetailService extends IService<ScheduleDetail> {
    IPage<ScheduleDetail> getList(ScheduleDetailPageParm parm);
    //List<ScheduleDetail> selectById(DoctorDetailParm doctorDetailParm);
    void subCount(Integer scheduleId);
    void addCount(Integer scheduleId);
}
