package com.itmk.netSystem.call.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.call.entity.MakeOrder;
import com.itmk.netSystem.call.entity.CallPage;
import com.itmk.netSystem.call.service.CallService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.netSystem.treatpatient.entity.VisitUser;
import org.apache.commons.lang.StringUtils;
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
        return ResultUtils.success("叫号");
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
                .orderByDesc(MakeOrder::getCreateTime);
        IPage<MakeOrder> list = callService.page(page, query);
        return ResultUtils.success("成功",list);
    }




}
