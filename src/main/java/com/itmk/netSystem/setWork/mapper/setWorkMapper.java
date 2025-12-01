package com.itmk.netSystem.setWork.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseMapper;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.entity.setWorkPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface setWorkMapper extends MPJBaseMapper<ScheduleDetail> {
    IPage<ScheduleDetail> getList(IPage<ScheduleDetail> page, @Param("parm") setWorkPage parm);

    /**
     * 批量取消排班 (通过将可预约数设为0).
     * @param scheduleIds 需要取消的排班ID列表
     * @return 影响的行数
     */
    int batchCancelSchedules(@Param("scheduleIds") List<Integer> scheduleIds);

    /**
     * 根据科室和日期聚合查询排班统计信息.
     * @param deptId 科室ID
     * @param date   查询日期
     * @return 包含总号源(totalAmount)和剩余号源(totalLastAmount)的Map
     */
    java.util.Map<String, Object> getScheduleSummaryByDepartment(@Param("deptId") Long deptId, @Param("date") String date);

    /**
     * 查询在指定日期没有排班的医生列表.
     * @param date 查询日期 (格式: yyyy-MM-dd)
     * @return 医生用户对象列表
     */
    List<com.itmk.netSystem.userWeb.entity.SysUser> findDoctorsWithoutSchedule(@Param("date") String date);

    /**
     * 查询指定日期范围内有排班的所有医生ID.
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 有排班的医生ID列表
     */
    List<Long> findScheduledDoctorIdsByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查找重复的排班数据 (同一医生在同一天被安排了多次).
     * @return 重复排班的列表，包含医生ID，日期和重复次数
     */
    List<java.util.Map<String, Object>> findDuplicateSchedules();
    void addCount(@Param("scheduleId") Integer scheduleId);
    void subCount(@Param("scheduleId") Integer scheduleId);


}
