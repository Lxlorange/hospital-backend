package com.itmk.web.schedule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.schedule.entity.ScheduleDetail;
import com.itmk.web.schedule.entity.ScheduleDetailPageParm;
//import com.itmk.web.phone.entity.DoctorDetailParm;

import java.util.List;

 
public interface ScheduleDetailService extends IService<ScheduleDetail> {
    IPage<ScheduleDetail> getList(ScheduleDetailPageParm parm);
    //List<ScheduleDetail> selectById(DoctorDetailParm doctorDetailParm);
    void subCount(Integer scheduleId);
    void addCount(Integer scheduleId);
}
