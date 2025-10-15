package com.itmk.netSystem.setWork.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.entity.setWorkPage;
//import com.itmk.web.phone.entity.DoctorDetailParm;


public interface setWorkService extends IService<ScheduleDetail> {
    IPage<ScheduleDetail> getList(setWorkPage parm);
    //List<ScheduleDetail> selectById(DoctorDetailParm doctorDetailParm);
    /**
     * 根据医生ID和日期范围查询排班列表
     * @param doctorId  医生ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 排班详情列表
     */
    java.util.List<ScheduleDetail> findSchedulesByDoctorAndDateRange(Long doctorId, String startDate, String endDate);

    /**
     * 复制指定医生某一周的排班到目标周
     * @param doctorId        医生ID
     * @param sourceStartDate 源周的开始日期 (例如周一)
     * @return 成功复制的条数
     */
    int copyWeeklySchedule(Long doctorId, java.time.LocalDate sourceStartDate);

    /**
     * 查找指定医生从今天起下一个可用的排班
     * @param doctorId 医生ID
     * @return 最早的一个可用排班详情，若无则返回null
     */
    ScheduleDetail findNextAvailableSlot(Long doctorId);

    /**
     * 取消指定医生在某一天的所有排班
     * @param doctorId 医生ID
     * @param date     指定日期
     * @return boolean 是否取消成功
     */
    boolean cancelDaySchedule(Long doctorId, String date);
    void subCount(Integer scheduleId);
    void addCount(Integer scheduleId);
}
