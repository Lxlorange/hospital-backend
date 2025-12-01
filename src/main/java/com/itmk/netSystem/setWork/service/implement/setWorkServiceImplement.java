package com.itmk.netSystem.setWork.service.implement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.phoneChat.entity.DoctorInformationNum;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.entity.setWorkPage;
import com.itmk.netSystem.setWork.mapper.setWorkMapper;
import com.itmk.netSystem.setWork.service.setWorkService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class setWorkServiceImplement extends ServiceImpl<setWorkMapper, ScheduleDetail> implements setWorkService {
    @Override
    public IPage<ScheduleDetail> getList(setWorkPage parm) {
        IPage<ScheduleDetail> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        return this.baseMapper.getList(page,parm);
    }

    @Override
    public List<ScheduleDetail> selectById(DoctorInformationNum doctorInformationNum) {
        return this.baseMapper.selectById(doctorInformationNum);
    }

    @Override
    public List<ScheduleDetail> selectByWorkId(Integer workId) {
        setWorkMapper workMapper = this.getBaseMapper();
        QueryWrapper<ScheduleDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("schedule_id",workId);
        return this.baseMapper.selectList(queryWrapper);
    }



    @Override
    public List<ScheduleDetail> findSchedulesByDoctorAndDateRange(Long doctorId, String startDate, String endDate) {
        QueryWrapper<ScheduleDetail> query = new QueryWrapper<>();
        query.lambda()
                .eq(ScheduleDetail::getDoctorId, doctorId)
                .between(ScheduleDetail::getTimes, startDate, endDate)
                .orderByAsc(ScheduleDetail::getTimes);
        return this.baseMapper.selectList(query);
    }

    @Override
    public int copyWeeklySchedule(Long doctorId, LocalDate sourceStartDate) {
        LocalDate sourceEndDate = sourceStartDate.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        // 1. 查出源周的排班
        List<ScheduleDetail> sourceSchedules = findSchedulesByDoctorAndDateRange(
                doctorId,
                sourceStartDate.format(formatter),
                sourceEndDate.format(formatter)
        );

        if(sourceSchedules.isEmpty()){
            return 0; // 源周没有排班，直接返回
        }

        // 2. 构造目标周的新排班列表
        List<ScheduleDetail> targetSchedules = sourceSchedules.stream().map(source -> {


            // 检查目标日期是否已有排班，避免重复
            QueryWrapper<ScheduleDetail> checkQuery = new QueryWrapper<>();

            if(this.baseMapper.selectCount(checkQuery) > 0){
                return null; // 目标日已有排班，跳过此条
            }

            ScheduleDetail target = new ScheduleDetail();
            target.setDoctorId(source.getDoctorId());

            target.setType(source.getType());
            target.setAmount(source.getAmount());
            target.setLastAmount(source.getLastAmount());
            return target;
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());

        // 3. 批量保存新排班
        if(!targetSchedules.isEmpty()){
            this.saveBatch(targetSchedules);
        }

        return targetSchedules.size();
    }

    @Override
    public ScheduleDetail findNextAvailableSlot(Long doctorId) {
        QueryWrapper<ScheduleDetail> query = new QueryWrapper<>();
        query.lambda()
                .eq(ScheduleDetail::getDoctorId, doctorId)
                .ge(ScheduleDetail::getTimes, LocalDate.now()) // 大于等于今天
                .gt(ScheduleDetail::getLastAmount, 0) // 剩余可预约数大于0
                .orderByAsc(ScheduleDetail::getTimes)
                .last("LIMIT 1"); // 获取第一条
        return this.baseMapper.selectOne(query);
    }

    @Override
    public boolean cancelDaySchedule(Long doctorId, String date) {
        QueryWrapper<ScheduleDetail> query = new QueryWrapper<>();
        query.lambda()
                .eq(ScheduleDetail::getDoctorId, doctorId)
                .eq(ScheduleDetail::getTimes, date);
        return this.remove(query);
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
