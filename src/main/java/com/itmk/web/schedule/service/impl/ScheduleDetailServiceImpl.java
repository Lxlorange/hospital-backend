package com.itmk.web.schedule.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.schedule.entity.ScheduleDetail;
import com.itmk.web.schedule.entity.ScheduleDetailPageParm;
import com.itmk.web.schedule.mapper.ScheduleDetailMapper;
import com.itmk.web.schedule.service.ScheduleDetailService;
import com.itmk.web.phone.entity.DoctorDetailParm;
import org.springframework.stereotype.Service;

import java.util.List;

 
@Service
public class ScheduleDetailServiceImpl extends ServiceImpl<ScheduleDetailMapper, ScheduleDetail> implements ScheduleDetailService {
    @Override
    public IPage<ScheduleDetail> getList(ScheduleDetailPageParm parm) {
        IPage<ScheduleDetail> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        return this.baseMapper.getList(page,parm);
    }

    @Override
    public List<ScheduleDetail> selectById(DoctorDetailParm doctorDetailParm) {
        return this.baseMapper.selectById(doctorDetailParm);
    }

    @Override
    public void subCount(Integer scheduleId) {
        this.baseMapper.subCount(scheduleId);
    }

    @Override
    public void addCount(Integer scheduleId) {
        this.baseMapper.addCount(scheduleId);
    }
}
