package com.itmk.netSystem.statistic.service;

import com.itmk.netSystem.statistic.dto.DateCount;
import com.itmk.netSystem.statistic.dto.NameCount;

import java.util.List;

public interface StatisticService {
    List<DateCount> registrationTrend(String startDate, String endDate);
    List<DateCount> visitTrend(String startDate, String endDate);
    List<NameCount> departmentDistribution(String startDate, String endDate);
    List<NameCount> doctorDistribution(String startDate, String endDate, Integer limit);
    List<NameCount> timesAreaDistribution(String startDate, String endDate);
    List<NameCount> statusDistribution(String startDate, String endDate);
}