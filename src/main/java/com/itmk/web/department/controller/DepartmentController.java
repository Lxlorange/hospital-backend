package com.itmk.web.department.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.department.entity.Department;
import com.itmk.web.department.entity.DepartmentPageParm;
import com.itmk.web.department.entity.SelectDept;
import com.itmk.web.department.service.DepartmentService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

 
@RequestMapping("/api/department")
@RestController
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    //查询科室下拉数据
    @GetMapping("/getSelectDept")
    public ResultVo getSelectDept(){
        QueryWrapper<Department> query = new QueryWrapper<>();
        query.lambda().orderByDesc(Department::getOrderNum);
        List<Department> list = departmentService.list(query);
        List<SelectDept> deptList = new ArrayList<>();
        if(list.size() > 0){
            for (int i=0;i<list.size();i++){
                SelectDept dept = new SelectDept();
                dept.setLabel(list.get(i).getDeptName());
                dept.setValue(list.get(i).getDeptId());
                deptList.add(dept);
            }

        }
        return ResultUtils.success("查询成功",deptList);
    }

    /**
     * 列表查询
     * @param parm
     * @return
     */
    @GetMapping("/getList")
    public ResultVo getList(DepartmentPageParm parm){
        //构造分页对象
        IPage<Department> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        //构造查询条件
        QueryWrapper<Department> query = new QueryWrapper<>();
        //模糊查询 like
        if(StringUtils.isNotEmpty(parm.getDeptName())){
            //like查询
            query.lambda().like(Department::getDeptName,parm.getDeptName());
        }
        //排序：order_num字段
        query.lambda().orderByDesc(Department::getOrderNum);
        IPage<Department> list = departmentService.page(page, query);
        return ResultUtils.success("查询成功!",list);
    }

    /**
     * 新增科室
     * @param department
     * @return
     */

    @PostMapping
    @PreAuthorize("hasAuthority('sys:department:add')")
    public ResultVo add(@RequestBody Department department){
        if(departmentService.save(department)){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }



    /**
     * 编辑科室
     * @param department
     * @return
     */
    @PutMapping
    @PreAuthorize("hasAuthority('sys:department:edit')")
    public ResultVo edit(@RequestBody Department department){
        if(departmentService.updateById(department)){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    //删除科室
    @PreAuthorize("hasAuthority('sys:department:delete')")
    @DeleteMapping("/{deptId}")
    public ResultVo delete(@PathVariable("deptId") Long deptId){
        if(departmentService.removeById(deptId)){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }
}
