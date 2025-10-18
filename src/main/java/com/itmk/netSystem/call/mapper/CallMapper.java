package com.itmk.netSystem.call.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.netSystem.call.entity.MakeOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CallMapper extends BaseMapper<MakeOrder> {
    Integer countAppointmentsByDoctorAndTimesArea(@Param("doctorId") Integer doctorId, @Param("timesArea") String timesArea, @Param("date") String date);

    int updateStatusById(@Param("makeId") Integer makeId, @Param("status") String status);

    List<MakeOrder> selectListByDoctorAndVisitStatus(@Param("doctorId") Integer doctorId, @Param("hasVisit") String hasVisit);

    MakeOrder selectOrderDetailById(Integer makeId);

    int batchUpdateCallStatus(@Param("makeIds") List<Integer> makeIds, @Param("hasCall") String hasCall);
}
