package com.itmk.netSystem.see.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.itmk.netSystem.call.entity.MakeOrder;
import com.itmk.netSystem.call.service.CallService;
import com.itmk.netSystem.see.entity.MakeOrderVisit;
import com.itmk.netSystem.see.entity.SeePage;
import com.itmk.netSystem.see.service.SeeService;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.treatpatient.entity.VisitUser;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

 
@RequestMapping("/api/makeOrderVisit")
@RestController
public class SeeController {
    @Autowired
    private SeeService seeService;
    @Autowired
    private userWebService userWebService;
    @Autowired
    private CallService callService;


    @PreAuthorize("hasAuthority('sys:visit:edit')")
    @PutMapping("/edit")
    @Transactional
    public ResultVo edit(@RequestBody MakeOrderVisit makeOrderVisit){
        makeOrderVisit.setVisitTime(new Date());
        seeService.updateById(makeOrderVisit);
        MakeOrder makeOrder = new MakeOrder();
        makeOrder.setMakeId(makeOrderVisit.getMakeId());
        makeOrder.setHasVisit("1");
        callService.updateById(makeOrder);
        return ResultUtils.success("成功!");
    }

    /**
     * 新增预约就诊记录
     * @param makeOrderVisit
     * @return
     */
    @PreAuthorize("hasAuthority('sys:visit:add')")
    @PostMapping("/add")
    public ResultVo add(@RequestBody MakeOrderVisit makeOrderVisit){
        makeOrderVisit.setCreateTime(new Date());
        if(seeService.save(makeOrderVisit)){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }

    /**
     * 根据ID删除就诊记录
     * @param visitId
     * @return
     */
    @PreAuthorize("hasAuthority('sys:visit:delete')")
    @DeleteMapping("/{visitId}")
    public ResultVo delete(@PathVariable Integer visitId) {
        if (seeService.removeById(visitId)) {
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }

    /**
     * 根据ID查询就诊详情
     * @param visitId
     * @return
     */
    @GetMapping("/{visitId}")
    public ResultVo<MakeOrderVisit> getById(@PathVariable Integer visitId) {
        MakeOrderVisit visit = seeService.getById(visitId);
        if (visit != null) {
            return ResultUtils.success("查询成功", visit);
        }
        return ResultUtils.error("未找到该记录");
    }

    /**
     * 获取某个病人的就诊历史
     * @param visitUserId
     * @return
     */
    @GetMapping("/history/{visitUserId}")
    public ResultVo getPatientHistory(@PathVariable Integer visitUserId) {
        return ResultUtils.success("查询成功", visitUserId);
    }

    /**
     * 获取医生即将到来的预约
     * @param doctorId
     * @return
     */
    @GetMapping("/upcoming/{doctorId}")
    public ResultVo getUpcomingAppointments(@PathVariable Integer doctorId) {
        return ResultUtils.success("查询成功", doctorId);
    }

    @GetMapping("/getList")
    public ResultVo getList(SeePage parm){
        SysUser user = userWebService.getById(parm.getDoctorId());
        if (user == null) {
            return ResultUtils.error("医生不存在");
        }
        IPage<MakeOrderVisit> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        MPJLambdaWrapper<MakeOrderVisit> query = new MPJLambdaWrapper<>();
        query.selectAll(MakeOrderVisit.class)
                .select(VisitUser::getVisitname)
                .select(Department::getDeptName)
                .select(SysUser::getNickName)
                .leftJoin(SysUser.class,SysUser::getUserId,MakeOrderVisit::getDoctorId)
                .leftJoin(VisitUser.class,VisitUser::getVisitId,MakeOrderVisit::getVisitUserId)
                .leftJoin(Department.class,Department::getDeptId,SysUser::getDeptId)
                .like(StringUtils.isNotEmpty(parm.getName()),VisitUser::getVisitname,parm.getName())
                .or()
                .like(StringUtils.isNotEmpty(parm.getName()),SysUser::getNickName,parm.getName())
                .eq(!user.getIsAdmin().equals("1"),MakeOrderVisit::getDoctorId,parm.getDoctorId())
                .eq(StringUtils.isNotEmpty(parm.getTimesArea()),MakeOrderVisit::getTimesArea,parm.getTimesArea())
                .orderByDesc(MakeOrderVisit::getCreateTime);
        IPage<MakeOrderVisit> list = seeService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }
}
