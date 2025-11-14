package com.itmk.netSystem.statistic.controller;

import com.itmk.netSystem.statistic.dto.DateCount;
import com.itmk.netSystem.statistic.dto.NameCount;
import com.itmk.netSystem.statistic.service.StatisticService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistic")
public class StatisticController {
    @Autowired
    private StatisticService statisticService;

    @GetMapping("/registrationTrend")
    public ResultVo<List<DateCount>> registrationTrend(@RequestParam(value = "startDate", required = false) String startDate,
                                                       @RequestParam(value = "endDate", required = false) String endDate) {
        return ResultUtils.success("success", statisticService.registrationTrend(startDate, endDate));
    }

    @GetMapping("/visitTrend")
    public ResultVo<List<DateCount>> visitTrend(@RequestParam(value = "startDate", required = false) String startDate,
                                                @RequestParam(value = "endDate", required = false) String endDate) {
        return ResultUtils.success("success", statisticService.visitTrend(startDate, endDate));
    }

    @GetMapping("/departmentDistribution")
    public ResultVo<List<NameCount>> departmentDistribution(@RequestParam(value = "startDate", required = false) String startDate,
                                                            @RequestParam(value = "endDate", required = false) String endDate) {
        return ResultUtils.success("success", statisticService.departmentDistribution(startDate, endDate));
    }

    @GetMapping("/doctorDistribution")
    public ResultVo<List<NameCount>> doctorDistribution(@RequestParam(value = "startDate", required = false) String startDate,
                                                        @RequestParam(value = "endDate", required = false) String endDate,
                                                        @RequestParam(value = "limit", required = false) Integer limit) {
        return ResultUtils.success("success", statisticService.doctorDistribution(startDate, endDate, limit));
    }

    @GetMapping("/timesAreaDistribution")
    public ResultVo<List<NameCount>> timesAreaDistribution(@RequestParam(value = "startDate", required = false) String startDate,
                                                           @RequestParam(value = "endDate", required = false) String endDate) {
        return ResultUtils.success("success", statisticService.timesAreaDistribution(startDate, endDate));
    }

    @GetMapping("/statusDistribution")
    public ResultVo<List<NameCount>> statusDistribution(@RequestParam(value = "startDate", required = false) String startDate,
                                                        @RequestParam(value = "endDate", required = false) String endDate) {
        return ResultUtils.success("success", statisticService.statusDistribution(startDate, endDate));
    }
}