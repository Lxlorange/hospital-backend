package com.itmk.web.setWork.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.setWork.entity.ScheduleDetail;
import com.itmk.web.setWork.entity.ScheduleDetailPageParm;
//import com.itmk.web.phone.entity.DoctorDetailParm;


public interface ScheduleDetailService extends IService<ScheduleDetail> {
    IPage<ScheduleDetail> getList(ScheduleDetailPageParm parm);
    //List<ScheduleDetail> selectById(DoctorDetailParm doctorDetailParm);
    void subCount(Integer scheduleId);
    void addCount(Integer scheduleId);
}
