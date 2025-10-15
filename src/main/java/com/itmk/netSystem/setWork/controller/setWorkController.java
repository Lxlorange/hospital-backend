package com.itmk.netSystem.setWork.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.entity.setWorkList;
import com.itmk.netSystem.setWork.service.setWorkService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

// 排班详情管理控制器
@RequestMapping("/api/scheduleDetail")
@RestController
public class setWorkController {
    @Autowired
    private setWorkService setWorkService; // 排班详情服务

    /**
     * 排班操作
     * @param scheduleDetail 包含排班信息的列表
     * @return ResultVo 操作结果
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sys:scheduleDetail:add')")
    public ResultVo add(@RequestBody List<ScheduleDetail> scheduleDetail){
        if(scheduleDetail.size() >0){
            for (int i=0;i<scheduleDetail.size();i++){
                // 查询该医生该日期是否已排班
                QueryWrapper<ScheduleDetail> query = new QueryWrapper<>();
                query.lambda().eq(ScheduleDetail::getDoctorId,scheduleDetail.get(i).getDoctorId())
                        .eq(ScheduleDetail::getTimes,scheduleDetail.get(i).getTimes());
                ScheduleDetail one = setWorkService.getOne(query);

                // 如果未排班则保存
                if(one == null){
                    // 如果是普通门诊，设置可预约数量为0
                    if(scheduleDetail.get(i).getType().equals("0")){
                        scheduleDetail.get(i).setAmount(0);
                        scheduleDetail.get(i).setLastAmount(0);
                    }
                    setWorkService.save(scheduleDetail.get(i));
                }
            }
        }
        return ResultUtils.success("成功");
    }

    /**
     * 编辑排班详情
     * @param scheduleDetail 待编辑的排班实体
     * @return ResultVo 操作结果
     */
    @PutMapping("/edit")
    @PreAuthorize("hasAuthority('sys:scheduleDetail:edit')")
    public ResultVo edit(@RequestBody ScheduleDetail scheduleDetail){
        if(setWorkService.updateById(scheduleDetail)){
            return ResultUtils.success("成功");
        }
        return ResultUtils.error("失败");
    }


    /**
     * 根据医生和日期范围查询排班
     * @param doctorId  医生ID
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate   结束日期 (格式: yyyy-MM-dd)
     * @return ResultVo 排班列表
     */
    @GetMapping("/doctor/{doctorId}")
    public ResultVo getSchedulesByDoctorAndDateRange(
            @PathVariable("doctorId") Long doctorId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String endDate) {
        List<ScheduleDetail> list = setWorkService.findSchedulesByDoctorAndDateRange(doctorId, startDate, endDate);
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 根据科室ID和指定日期查询排班
     * @param deptId 科室ID
     * @param date   指定日期 (格式: yyyy-MM-dd)
     * @return ResultVo 排班列表
     */
    @GetMapping("/department/{deptId}")
    public ResultVo getSchedulesByDepartmentAndDate(
            @PathVariable("deptId") Long deptId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String date) {

        MPJLambdaWrapper<ScheduleDetail> query = new MPJLambdaWrapper<>();
        query.selectAll(ScheduleDetail.class)
                .select(SysUser::getNickName) // 额外查询医生姓名
                .leftJoin(SysUser.class, SysUser::getUserId, ScheduleDetail::getDoctorId)
                .eq(SysUser::getDeptId, deptId)
                .eq(ScheduleDetail::getTimes, date)
                .gt(ScheduleDetail::getLastAmount, 0); // 只查询有余号的排班

        List<ScheduleDetail> list = setWorkService.list(query);
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 复制医生周排班到下一周
     * @param doctorId 医生ID
     * @param sourceStartDate 源周的开始日期 (格式：yyyy-MM-dd)，通常是周一
     * @return ResultVo 操作结果
     */
    @PostMapping("/copyWeek")
    @PreAuthorize("hasAuthority('sys:scheduleDetail:add')")
    public ResultVo copyWeek(
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("sourceStartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sourceStartDate) {
        int count = setWorkService.copyWeeklySchedule(doctorId, sourceStartDate);
        return ResultUtils.success("成功复制 " + count + " 条排班记录");
    }

    /**
     * 查询医生下一个可预约的排班
     * @param doctorId 医生ID
     * @return ResultVo 可预约的排班信息
     */
    @GetMapping("/doctor/{doctorId}/nextAvailable")
    public ResultVo getNextAvailableSlot(@PathVariable("doctorId") Long doctorId) {
        ScheduleDetail schedule = setWorkService.findNextAvailableSlot(doctorId);
        if (schedule != null) {
            return ResultUtils.success("查询成功", schedule);
        }
        return ResultUtils.error("未找到该医生未来的可预约排班");
    }

    /**
     * 取消医生某一天的所有排班
     * @param doctorId 医生ID
     * @param date     指定日期
     * @return ResultVo 操作结果
     */
    @DeleteMapping("/cancelDay")
    @PreAuthorize("hasAuthority('sys:scheduleDetail:delete')")
    public ResultVo cancelDay(
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String date) {
        if (setWorkService.cancelDaySchedule(doctorId, date)) {
            return ResultUtils.success("取消成功");
        }
        return ResultUtils.error("取消失败或当天无排班");
    }

    /**
     * 列表分页查询
     * @param parm 分页及查询参数
     * @return ResultVo 包含分页数据的列表
     */
    @GetMapping("/getList")
    public ResultVo getList(setWorkList parm){
        // 构造分页对象
        IPage<ScheduleDetail> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        // 构造查询条件 (联查排班、医生和科室)
        MPJLambdaWrapper<ScheduleDetail> query = new MPJLambdaWrapper<>();
        query.selectAll(ScheduleDetail.class)
                .select(Department::getDeptName,Department::getDeptId)
                .leftJoin(SysUser.class,SysUser::getUserId,ScheduleDetail::getDoctorId) // 联查医生信息
                .leftJoin(Department.class,Department::getDeptId,SysUser::getDeptId); // 联查科室信息

        // 模糊查询医生昵称
        if(StringUtils.isNotEmpty(parm.getDoctorName())){
            query.like(SysUser::getNickName,parm.getDoctorName());
        }
        // 按排班日期倒序
        query.orderByDesc(ScheduleDetail::getTimes);

        IPage<ScheduleDetail> list = setWorkService.page(page, query);
        return ResultUtils.success("成功",list);
    }

    // 删除单个排班
    @PreAuthorize("hasAuthority('sys:scheduleDetail:delete')")
    @DeleteMapping("/{scheduleId}")
    public ResultVo delete(@PathVariable("scheduleId") Long scheduleId){
        if(setWorkService.removeById(scheduleId)){
            return ResultUtils.success("成功");
        }
        return ResultUtils.error("失败");
    }

    // 批量删除排班
    @PreAuthorize("hasAuthority('sys:scheduleDetail:delList')")
    @PostMapping("delList")
    public ResultVo delList(@RequestBody List<Integer> list) {
        if(setWorkService.removeByIds(list)){
            return ResultUtils.success("成功");
        }
        return ResultUtils.error("失败");
    }
}