package com.itmk.netSystem.see.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.netSystem.see.entity.MakeOrderVisit;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


public interface SeeMapper extends BaseMapper<MakeOrderVisit> {
    /**
     * 根据病人ID查询其所有就诊记录
     * @param visitUserId
     * @return
     */
    List<MakeOrderVisit> findHistoryByPatientId(@Param("visitUserId") Integer visitUserId);

    /**
     * 查询某医生所有未就诊的预约
     * @param doctorId
     * @return
     */
    List<MakeOrderVisit> findUpcomingAppointmentsByDoctorId(@Param("doctorId") Integer doctorId);

    /**
     * 批量更新就诊状态
     * @param visitIds
     * @param hasVisit
     * @return
     */
    int batchUpdateVisitStatus(@Param("visitIds") List<Integer> visitIds, @Param("hasVisit") String hasVisit);

    /**
     * 统计指定日期范围内的就诊数量
     * @param startDate
     * @param endDate
     * @return
     */
    Long countVisitsBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 获取所有需要住院的就诊记录
     * @return
     */
    List<MakeOrderVisit> selectAllHospitalizedVisits();
}
