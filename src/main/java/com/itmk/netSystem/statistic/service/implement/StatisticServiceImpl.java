package com.itmk.netSystem.statistic.service.implement;

import com.itmk.netSystem.statistic.dto.DateCount;
import com.itmk.netSystem.statistic.dto.NameCount;
import com.itmk.netSystem.statistic.mapper.StatisticMapper;
import com.itmk.netSystem.statistic.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatisticServiceImpl implements StatisticService {
    @Autowired
    private StatisticMapper statisticMapper;

    private String defaultStart(int days) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days);
        return start.format(DateTimeFormatter.ISO_DATE);
    }

    private String today() {
        return LocalDate.now().format(DateTimeFormatter.ISO_DATE);
    }

    @Override
    public List<DateCount> registrationTrend(String startDate, String endDate) {
        String s = startDate != null ? startDate : defaultStart(30);
        String e = endDate != null ? endDate : today();
        return statisticMapper.getRegistrationTrend(s, e);
    }

    @Override
    public List<DateCount> visitTrend(String startDate, String endDate) {
        String s = startDate != null ? startDate : defaultStart(30);
        String e = endDate != null ? endDate : today();
        return statisticMapper.getVisitTrend(s, e);
    }

    @Override
    public List<NameCount> departmentDistribution(String startDate, String endDate) {
        String s = startDate != null ? startDate : defaultStart(90);
        String e = endDate != null ? endDate : today();
        return statisticMapper.getDepartmentDistribution(s, e);
    }

    @Override
    public List<NameCount> doctorDistribution(String startDate, String endDate, Integer limit) {
        String s = startDate != null ? startDate : defaultStart(90);
        String e = endDate != null ? endDate : today();
        Integer l = (limit == null || limit <= 0) ? 10 : limit;
        return statisticMapper.getDoctorDistribution(s, e, l);
    }

    @Override
    public List<NameCount> timesAreaDistribution(String startDate, String endDate) {
        String s = startDate != null ? startDate : defaultStart(90);
        String e = endDate != null ? endDate : today();
        return statisticMapper.getTimesAreaDistribution(s, e);
    }

    @Override
    public List<NameCount> statusDistribution(String startDate, String endDate) {
        String s = startDate != null ? startDate : defaultStart(90);
        String e = endDate != null ? endDate : today();
        return statisticMapper.getStatusDistribution(s, e);
    }
}