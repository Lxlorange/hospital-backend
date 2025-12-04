package com.itmk.netSystem.call.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.itmk.netSystem.call.entity.CallPage;
import com.itmk.netSystem.call.entity.MakeOrder;
import com.itmk.netSystem.call.service.CallService;
import com.itmk.netSystem.see.entity.MakeOrderVisit;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.treatpatient.entity.VisitUser;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/api/makeOrder")
@RestController
public class CallController {
    @Autowired
    private CallService callService;
    @Autowired
    private userWebService userWebService;


    @PostMapping("/callVisit")
    @PreAuthorize("hasAuthority('sys:makeOrder:call')")
    public ResultVo callVisit(@RequestBody MakeOrder makeOrder){
        callService.callVisit(makeOrder);
        MakeOrder updated = callService.getMakeOrderDetail(makeOrder.getMakeId());
        return ResultUtils.success("叫号", updated);
    }

    @GetMapping("/{makeId}")
    public ResultVo getDetail(@PathVariable("makeId") Integer makeId) {
        MakeOrder detail = callService.getMakeOrderDetail(makeId);
        if (detail != null) {
            return ResultUtils.success("查询成功", detail);
        }
        return ResultUtils.error("预约信息不存在!");
    }

    @PutMapping("/cancel/{makeId}")
    @PreAuthorize("hasAuthority('sys:makeOrder:cancel')")
    public ResultVo cancel(@PathVariable("makeId") Integer makeId) {
        if (callService.cancelAppointment(makeId)) {
            return ResultUtils.success("取消成功!");
        }
        return ResultUtils.error("取消失败!");
    }

    @PutMapping("/updateVisitStatus/{makeId}/{hasVisitStatus}")
    @PreAuthorize("hasAuthority('sys:makeOrder:updateVisit')")
    public ResultVo updateVisitStatus(@PathVariable("makeId") Integer makeId, @PathVariable("hasVisitStatus") String hasVisitStatus) {
        if (callService.updateVisitStatus(makeId, hasVisitStatus)) {
            return ResultUtils.success("更新就诊状态成功!");
        }
        return ResultUtils.error("更新就诊状态失败!");
    }

    @PreAuthorize("hasAuthority('sys:makeOrder:delete')")
    @DeleteMapping("/{makeId}")
    public ResultVo delete(@PathVariable("makeId") Integer makeId){
        if(callService.removeById(makeId)){
            return ResultUtils.success("成功");
        }
        return ResultUtils.error("失败");
    }

    @GetMapping("/pendingList/{doctorId}")
    @PreAuthorize("hasAuthority('sys:makeOrder:pendingList')")
    public ResultVo getPendingList(@PathVariable("doctorId") Integer doctorId) {
        List<MakeOrder> list = callService.listPendingAppointmentsByDoctor(doctorId);
        return ResultUtils.success("查询待就诊列表成功", list);
    }

    @GetMapping("/queue/{scheduleId}")
    @PreAuthorize("hasAuthority('sys:makeOrder:pendingList')")
    public ResultVo getScheduleQueue(@PathVariable("scheduleId") Integer scheduleId) {
        List<MakeOrder> list = callService.listScheduleQueue(scheduleId);
        return ResultUtils.success("查询排班签到队列成功", list);
    }

    @GetMapping("/userHistory")
    public ResultVo getUserHistory(CallPage parm) {
        IPage<MakeOrder> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        IPage<MakeOrder> list = callService.getAppointmentHistoryByUserId(page, parm.getUserId());
        return ResultUtils.success("查询用户历史预约成功", list);
    }


    @GetMapping("/getList")
    public ResultVo getList(CallPage parm){
        SysUser user = userWebService.getById(parm.getDoctorId());
        IPage<MakeOrder> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        MPJLambdaWrapper<MakeOrder> query = new MPJLambdaWrapper<>();
        query.selectAll(MakeOrder.class)
                .select(VisitUser::getVisitname)
                .select(Department::getDeptName)
                .select(SysUser::getNickName)
                .leftJoin(SysUser.class,SysUser::getUserId,MakeOrder::getDoctorId)
                .leftJoin(VisitUser.class,VisitUser::getVisitId,MakeOrder::getVisitUserId)
                .leftJoin(Department.class,Department::getDeptId,SysUser::getDeptId)
                .like(StringUtils.isNotEmpty(parm.getName()),VisitUser::getVisitname,parm.getName())
                .or()
                .like(StringUtils.isNotEmpty(parm.getName()),SysUser::getNickName,parm.getName())
                .eq(!user.getIsAdmin().equals("1"),MakeOrder::getDoctorId,parm.getDoctorId())
                .eq(StringUtils.isNotEmpty(parm.getTimesArea()),MakeOrder::getTimesArea,parm.getTimesArea())
                .orderByAsc(MakeOrder::getTimes)
                .orderByAsc(MakeOrder::getTimesArea)
                .orderByAsc(MakeOrder::getCreateTime)
                .orderByAsc(MakeOrder::getMakeId);
        IPage<MakeOrder> list = callService.page(page, query);
        return ResultUtils.success("成功",list);
    }

    @PostMapping("/checkIn")
    @PreAuthorize("hasAuthority('sys:makeOrder:call')")
    public ResultVo checkIn(@RequestBody MakeOrder makeOrder) {
        if (callService.checkIn(makeOrder.getMakeId())) {
            return ResultUtils.success("签到成功!");
        }
        return ResultUtils.error("签到失败!");
    }

    @PostMapping("/callNext/{scheduleId}")
    @PreAuthorize("hasAuthority('sys:makeOrder:call')")
    public ResultVo callNext(@PathVariable("scheduleId") Integer scheduleId) {
        MakeOrder called = callService.callNext(scheduleId);
        if (called == null) {
            return ResultUtils.error("当前无已签到的待叫号患者");
        }
        return ResultUtils.success("叫号", called);
    }

    /**
     * 返回该患者的【所有就诊记录】 (全院历史)
     * 场景：医生查看患者既往病史，不限科室和医生。
     */
    @GetMapping("/getAllHistory")
    public ResultVo getAllHistory(CallPage parm) {
        // 初始化分页参数
        parm.normalizePageParams();
        IPage<MakeOrder> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());

        MPJLambdaWrapper<MakeOrder> query = new MPJLambdaWrapper<>();
        query.selectAll(MakeOrder.class)
                .select(MakeOrderVisit::getAdvice, MakeOrderVisit::getVisitTime)
                .select(Department::getDeptName)
                .select(SysUser::getNickName)
                .select(VisitUser::getVisitname)
                .leftJoin(MakeOrderVisit.class, MakeOrderVisit::getMakeId, MakeOrder::getMakeId)
                .leftJoin(SysUser.class, SysUser::getUserId, MakeOrder::getDoctorId)
                .leftJoin(Department.class, Department::getDeptId, SysUser::getDeptId)
                .leftJoin(VisitUser.class, VisitUser::getVisitId, MakeOrder::getVisitUserId)
                .eq(parm.getVisitUserId() != null, MakeOrder::getVisitUserId, parm.getVisitUserId())
                // 通常只看“已就诊”的历史，如果前端传了status则用前端的，否则默认查已就诊(has_visit=1)
                .eq(StringUtils.isNotEmpty(parm.getStatus()), MakeOrder::getHasVisit, parm.getStatus())
                .eq(StringUtils.isEmpty(parm.getStatus()), MakeOrder::getHasVisit, "1")

                .orderByDesc(MakeOrder::getCreateTime);

        IPage<MakeOrder> list = callService.page(page, query);
        return ResultUtils.success("查询全院记录成功", list);
    }

    /**
     * 返回该患者在【当前科室】的就诊记录
     * 场景：医生只想看该患者在本科室（比如骨科）看过的记录，包括同事看的。
     */
    @GetMapping("/getDeptHistory")
    public ResultVo getDeptHistory(CallPage parm) {
        parm.normalizePageParams();

        // 获取当前医生所属的科室ID
        SysUser currentDoctor = userWebService.getById(parm.getDoctorId());
        if (currentDoctor == null || currentDoctor.getDeptId() == null) {
            return ResultUtils.error("医生信息不完整，无法获取科室信息");
        }
        Integer targetDeptId = currentDoctor.getDeptId();

        IPage<MakeOrder> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        MPJLambdaWrapper<MakeOrder> query = new MPJLambdaWrapper<>();

        query.selectAll(MakeOrder.class)
                .select(MakeOrderVisit::getAdvice, MakeOrderVisit::getVisitTime)
                .select(Department::getDeptName)
                .select(SysUser::getNickName) // 显示当时是谁看的
                .select(VisitUser::getVisitname)
                .leftJoin(MakeOrderVisit.class, MakeOrderVisit::getMakeId, MakeOrder::getMakeId)
                .leftJoin(SysUser.class, SysUser::getUserId, MakeOrder::getDoctorId)
                .leftJoin(Department.class, Department::getDeptId, SysUser::getDeptId)
                .leftJoin(VisitUser.class, VisitUser::getVisitId, MakeOrder::getVisitUserId)


                .eq(parm.getVisitUserId() != null, MakeOrder::getVisitUserId, parm.getVisitUserId())
                .eq(SysUser::getDeptId, targetDeptId)
                .eq(StringUtils.isNotEmpty(parm.getStatus()), MakeOrder::getHasVisit, parm.getStatus())
                .eq(StringUtils.isEmpty(parm.getStatus()), MakeOrder::getHasVisit, "1")

                .orderByDesc(MakeOrder::getCreateTime);

        IPage<MakeOrder> list = callService.page(page, query);
        return ResultUtils.success("查询本科室记录成功", list);
    }

    /**
     * 返回该患者在当前医生自己这里的就诊记录
     * 用于查看复诊记录，自己给该患者看过什么病
     */
    @GetMapping("/getMyHistory")
    public ResultVo getMyHistory(CallPage parm) {
        parm.normalizePageParams();
        SysUser currentDoctor=userWebService.getById(parm.getDoctorId());
        if (currentDoctor == null ) {
            return ResultUtils.error("医生信息不完整，无法获取信息");
        }
        IPage<MakeOrder> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        MPJLambdaWrapper<MakeOrder> query = new MPJLambdaWrapper<>();
        query.selectAll(MakeOrder.class)
                .select(MakeOrderVisit::getAdvice, MakeOrderVisit::getVisitTime)
                .select(Department::getDeptName)
                .select(SysUser::getNickName) // 显示当时是谁看的
                .select(VisitUser::getVisitname)
                .leftJoin(SysUser.class, SysUser::getUserId, MakeOrder::getDoctorId)
                .leftJoin(Department.class, Department::getDeptId, SysUser::getDeptId)
                .leftJoin(MakeOrderVisit.class, MakeOrderVisit::getMakeId, MakeOrder::getMakeId)
                .leftJoin(VisitUser.class, VisitUser::getVisitId, MakeOrder::getVisitUserId)
                .eq(parm.getVisitUserId() != null, MakeOrder::getVisitUserId, parm.getVisitUserId())
                .eq(MakeOrder::getDoctorId,parm.getDoctorId())
                .eq(StringUtils.isNotEmpty(parm.getStatus()), MakeOrder::getHasVisit, parm.getStatus())
                .eq(StringUtils.isEmpty(parm.getStatus()), MakeOrder::getHasVisit, "1")
                .orderByDesc(MakeOrder::getCreateTime);
        IPage<MakeOrder> list=callService.page(page,query);
        return ResultUtils.success("查询本科室记录成功", list);
    }




}
