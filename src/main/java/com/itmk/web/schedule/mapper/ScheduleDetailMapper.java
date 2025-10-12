package com.itmk.web.schedule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.web.schedule.entity.ScheduleDetail;
import com.itmk.web.schedule.entity.ScheduleDetailPageParm;
//import com.itmk.web.phone.entity.DoctorDetailParm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

 
public interface ScheduleDetailMapper extends BaseMapper<ScheduleDetail> {
    IPage<ScheduleDetail> getList(IPage<ScheduleDetail> page, @Param("parm") ScheduleDetailPageParm parm);
    //List<ScheduleDetail> selectById(@Param("parm") DoctorDetailParm doctorDetailParm);
    //减数量
    void subCount(@Param("scheduleId") Integer scheduleId);
    //加数量
    void addCount(@Param("scheduleId") Integer scheduleId);
}
