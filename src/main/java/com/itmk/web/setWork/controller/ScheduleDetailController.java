package com.itmk.web.setWork.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.department.entity.Department;
import com.itmk.web.setWork.entity.ScheduleDetail;
import com.itmk.web.setWork.entity.ScheduleDetailListParm;
import com.itmk.web.setWork.service.ScheduleDetailService;
import com.itmk.web.userWeb.entity.SysUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

 
@RequestMapping("/api/scheduleDetail")
@RestController
public class ScheduleDetailController {
    @Autowired
    private ScheduleDetailService scheduleDetailService;

    /**
     * 列表查询
     * @param parm
     * @return
     */
    @GetMapping("/getList")
    public ResultVo getList(ScheduleDetailListParm parm){
        //构造分页对象
        IPage<ScheduleDetail> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        //构造查询条件
        MPJLambdaWrapper<ScheduleDetail> query = new MPJLambdaWrapper<>();
        query.selectAll(ScheduleDetail.class)
                .select(Department::getDeptName,Department::getDeptId)
                .leftJoin(SysUser.class,SysUser::getUserId,ScheduleDetail::getDoctorId)
                .leftJoin(Department.class,Department::getDeptId,SysUser::getDeptId);
        //模糊查询 like
        if(StringUtils.isNotEmpty(parm.getDoctorName())){
            //like查询
            query.like(SysUser::getNickName,parm.getDoctorName());
        }
        //排序：order_num字段
        query.orderByDesc(ScheduleDetail::getTimes);
        IPage<ScheduleDetail> list = scheduleDetailService.page(page, query);
        return ResultUtils.success("查询成功!",list);
    }

    /**
     * 排班
     * @param scheduleDetail
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sys:scheduleDetail:add')")
    public ResultVo add(@RequestBody List<ScheduleDetail> scheduleDetail){
        if(scheduleDetail.size() >0){
            for (int i=0;i<scheduleDetail.size();i++){
                //查询一下，该日期是否已经排班
                QueryWrapper<ScheduleDetail> query = new QueryWrapper<>();
                query.lambda().eq(ScheduleDetail::getDoctorId,scheduleDetail.get(i).getDoctorId())
                        .eq(ScheduleDetail::getTimes,scheduleDetail.get(i).getTimes());
                ScheduleDetail one = scheduleDetailService.getOne(query);
                if(one == null){
                    if(scheduleDetail.get(i).getType().equals("0")){
                        scheduleDetail.get(i).setAmount(0);
                        scheduleDetail.get(i).setLastAmount(0);
                    }
                    scheduleDetailService.save(scheduleDetail.get(i));
                }

            }
        }
        return ResultUtils.success("排班成功!");
    }

    /**
     * 编辑
     * @param scheduleDetail
     * @return
     */
    @PutMapping("/edit")
    @PreAuthorize("hasAuthority('sys:scheduleDetail:edit')")
    public ResultVo edit(@RequestBody ScheduleDetail scheduleDetail){
        if(scheduleDetailService.updateById(scheduleDetail)){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    //删除
    @PreAuthorize("hasAuthority('sys:scheduleDetail:delete')")
    @DeleteMapping("/{scheduleId}")
    public ResultVo delete(@PathVariable("scheduleId") Long scheduleId){
        if(scheduleDetailService.removeById(scheduleId)){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }

    //批量删除
    @PreAuthorize("hasAuthority('sys:scheduleDetail:delList')")
    @PostMapping("delList")
    public ResultVo delList(@RequestBody List<Integer> list) {
        if(scheduleDetailService.removeByIds(list)){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }
}
