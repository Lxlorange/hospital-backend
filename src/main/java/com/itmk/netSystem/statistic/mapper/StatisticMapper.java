package com.itmk.netSystem.statistic.mapper;

import com.itmk.netSystem.statistic.dto.DateCount;
import com.itmk.netSystem.statistic.dto.NameCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StatisticMapper {

    @Select("SELECT mo.times AS date, COUNT(1) AS count " +
            "FROM make_order mo " +
            "WHERE mo.times BETWEEN #{startDate} AND #{endDate} AND mo.status = '1' " +
            "GROUP BY mo.times ORDER BY mo.times")
    List<DateCount> getRegistrationTrend(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT DATE_FORMAT(mv.visit_time, '%Y-%m-%d') AS date, COUNT(1) AS count " +
            "FROM make_order_visit mv " +
            "WHERE mv.visit_time BETWEEN #{startDate} AND #{endDate} AND mv.has_visit = '1' " +
            "GROUP BY DATE_FORMAT(mv.visit_time, '%Y-%m-%d') ORDER BY date")
    List<DateCount> getVisitTrend(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT d.dept_name AS name, COUNT(1) AS count " +
            "FROM make_order mo " +
            "LEFT JOIN sys_user u ON mo.doctor_id = u.user_id " +
            "LEFT JOIN department d ON u.dept_id = d.dept_id " +
            "WHERE mo.times BETWEEN #{startDate} AND #{endDate} AND mo.status = '1' " +
            "GROUP BY d.dept_id, d.dept_name ORDER BY count DESC")
    List<NameCount> getDepartmentDistribution(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT u.nick_name AS name, COUNT(1) AS count " +
            "FROM make_order mo " +
            "LEFT JOIN sys_user u ON mo.doctor_id = u.user_id " +
            "WHERE mo.times BETWEEN #{startDate} AND #{endDate} AND mo.status = '1' " +
            "GROUP BY u.user_id, u.nick_name ORDER BY count DESC LIMIT #{limit}")
    List<NameCount> getDoctorDistribution(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("limit") Integer limit);

    @Select("SELECT CASE mo.times_area WHEN '0' THEN '上午' WHEN '1' THEN '下午' ELSE '未知' END AS name, COUNT(1) AS count " +
            "FROM make_order mo " +
            "WHERE mo.times BETWEEN #{startDate} AND #{endDate} AND mo.status = '1' " +
            "GROUP BY mo.times_area")
    List<NameCount> getTimesAreaDistribution(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT CASE mo.status WHEN '1' THEN '成功' WHEN '2' THEN '取消' ELSE '其他' END AS name, COUNT(1) AS count " +
            "FROM make_order mo " +
            "WHERE mo.times BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY mo.status")
    List<NameCount> getStatusDistribution(@Param("startDate") String startDate, @Param("endDate") String endDate);
}