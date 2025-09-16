package com.itmk.web.make_order_visit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.department.entity.Department;
import com.itmk.web.make_order.entity.MakeOrder;
import com.itmk.web.make_order.service.MakeOrderService;
import com.itmk.web.make_order_visit.entity.MakeOrderVisit;
import com.itmk.web.make_order_visit.entity.MakeOrderVisitPageParm;
import com.itmk.web.make_order_visit.service.MakeOrderVisitService;
import com.itmk.web.sys_user.entity.SysUser;
import com.itmk.web.sys_user.service.SysUserService;
import com.itmk.web.visit_user.entity.VisitUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

 
@RequestMapping("/api/makeOrderVisit")
@RestController
public class MakeOrderVisitController {
    @Autowired
    private MakeOrderVisitService makeOrderVisitService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private MakeOrderService makeOrderService;

    /**
     * 就诊
     * @param makeOrderVisit
     * @return
     */
    @PreAuthorize("hasAuthority('sys:visit:edit')")
    @PutMapping("/edit")
    @Transactional
    public ResultVo edit(@RequestBody MakeOrderVisit makeOrderVisit){
        makeOrderVisit.setVisitTime(new Date());
        makeOrderVisitService.updateById(makeOrderVisit);
        MakeOrder makeOrder = new MakeOrder();
        makeOrder.setMakeId(makeOrderVisit.getMakeId());
        makeOrder.setHasVisit("1");
        makeOrderService.updateById(makeOrder);
        return ResultUtils.success("编写成功!");
    }

    @GetMapping("/getList")
    public ResultVo getList(MakeOrderVisitPageParm parm){
        //医生信息
        SysUser user = sysUserService.getById(parm.getDoctorId());
        //构造分页对象
        IPage<MakeOrderVisit> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        //构造查询条件
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
        IPage<MakeOrderVisit> list = makeOrderVisitService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }
}
