package com.itmk.netSystem.plat.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.netSystem.department.service.DepartmentService;
import com.itmk.netSystem.plat.entity.TotalVo;
/*import com.itmk.web.patientOrder.service.MakeOrderVisitService;
import com.itmk.web.informationWeb.entity.SysNotice;
import com.itmk.web.informationWeb.service.SysNoticeService;
import com.itmk.web.userPhone.service.WxUserService;*/
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.service.ScheduleDetailService;
import com.itmk.netSystem.userWeb.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

 
@RestController
@RequestMapping("/api/home")
public class HomeController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private SysUserService sysUserService;
    /*@Autowired
    //private WxUserService wxUserService;
    @Autowired
    private MakeOrderVisitService makeOrderVisitService;
    @Autowired
    private SysNoticeService sysNoticeService;*/
    @Autowired
    private ScheduleDetailService scheduleDetailService;

    /**
     * 上周、本周、下周
     * 查询上周、本周、下周数据
     * @param type
     * @param doctorId
     * @return
     */
    @GetMapping("/getMySchedule")
    public ResultVo getMySchedule(String type,Integer doctorId){
        // 获取今天的日期
        LocalDate today = LocalDate.now();
        List<ScheduleDetail> list = null;
        switch (type){
            case "1":
                // 获取本周的起始日期（周一）
                LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                // 获取本周的结束日期（周日）
                LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                QueryWrapper<ScheduleDetail> query = new QueryWrapper<>();
                query.lambda().eq(ScheduleDetail::getDoctorId,doctorId)
                        .between(ScheduleDetail::getTimes,startOfWeek.toString(),endOfWeek.toString());
                list = scheduleDetailService.list(query);
                break;
            case "2":
                // 获取下周的起始日期（下周一）
                LocalDate startOfNextWeek = today.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

                // 获取下周的结束日期（下周日）
                LocalDate endOfNextWeek = today.plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                QueryWrapper<ScheduleDetail> nextquery = new QueryWrapper<>();
                nextquery.lambda().eq(ScheduleDetail::getDoctorId,doctorId)
                        .between(ScheduleDetail::getTimes,startOfNextWeek.toString(),endOfNextWeek.toString());
                list = scheduleDetailService.list(nextquery);
                break;
            default:

                // 获取上周的起始日期（上周一）
                LocalDate startOfLastWeek = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

                // 获取上周的结束日期（上周日）
                LocalDate endOfLastWeek = today.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

                QueryWrapper<ScheduleDetail> querys = new QueryWrapper<>();
                querys.lambda().eq(ScheduleDetail::getDoctorId,doctorId)
                        .between(ScheduleDetail::getTimes,startOfLastWeek.toString(),endOfLastWeek.toString());
                list = scheduleDetailService.list(querys);
        }
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 首页公告
     * @return
     */

    /*@GetMapping("/getIndexNotice")
    public ResultVo getIndexNotice(){
        QueryWrapper<SysNotice> query = new QueryWrapper<>();
        query.lambda().orderByDesc(SysNotice::getCreateTime)
                .last(" limit 3");
        List<SysNotice> list = sysNoticeService.list(query);
        return ResultUtils.success("查询成功",list);
    }*/

    /**
     * 总数统计
     * @return
     */
    @GetMapping("/getHomeTotal")
    public ResultVo getHomeTotal(){
        TotalVo vo = new TotalVo();
        //科室总数
        long count = departmentService.count();
        vo.setDepartmentCount(count);
        //医生总数
        long ucount = sysUserService.count();
        vo.setSysUserCount(ucount);
        //患者总数
        /*long wcount = wxUserService.count();
        vo.setWxUserCount(wcount);
        //就诊次数
        long mcount = makeOrderVisitService.count();
        vo.setVisitCount(mcount);*/
        return ResultUtils.success("查询成功",vo);
    }

}
