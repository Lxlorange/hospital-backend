package com.itmk.web.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.department.entity.Department;
import com.itmk.web.order.entity.MakeOrder;
import com.itmk.web.order.entity.MakeOrderPageParm;
import com.itmk.web.order.service.MakeOrderService;
import com.itmk.web.userWeb.entity.SysUser;
import com.itmk.web.userWeb.service.SysUserService;
import com.itmk.web.userPatient.entity.VisitUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

 
@RequestMapping("/api/makeOrder")
@RestController
public class MakeOrderController {
    @Autowired
    private MakeOrderService makeOrderService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 叫号
     * @return
     */
    @PostMapping("/callVisit")
    @PreAuthorize("hasAuthority('sys:makeOrder:call')")
    public ResultVo callVisit(@RequestBody MakeOrder makeOrder){
        makeOrderService.callVisit(makeOrder);
        return ResultUtils.success("已叫号!");
    }

    //删除
    @PreAuthorize("hasAuthority('sys:makeOrder:delete')")
    @DeleteMapping("/{makeId}")
    public ResultVo delete(@PathVariable("makeId") Integer makeId){
        if(makeOrderService.removeById(makeId)){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }

    @GetMapping("/getList")
    public ResultVo getList(MakeOrderPageParm parm){
        //查询医生信息
        SysUser user = sysUserService.getById(parm.getDoctorId());
        //构造分页对象
        IPage<MakeOrder> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        //构造查询条件
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
        IPage<MakeOrder> list = makeOrderService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }
}
