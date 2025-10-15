package com.itmk.netSystem.teamDepartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.teamDepartment.entity.teamDepartmentPage;
import com.itmk.netSystem.teamDepartment.entity.teamDepartment;
import com.itmk.netSystem.teamDepartment.service.teamDepartmentService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// 科室管理控制器
@RequestMapping("/api/department")
@RestController
public class teamDepartmentController {
    @Autowired
    private teamDepartmentService teamDepartmentService; // 科室服务

    /**
     * 新增科室
     * @param department 待新增的科室实体
     * @return ResultVo 操作结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:department:add')")
    public ResultVo add(@RequestBody Department department){
        if(teamDepartmentService.save(department)){
            return ResultUtils.success("成功");
        }
        return ResultUtils.error("失败");
    }

    /**
     * 编辑科室
     * @param department 待编辑的科室实体
     * @return ResultVo 操作结果
     */
    @PutMapping
    @PreAuthorize("hasAuthority('sys:department:edit')")
    public ResultVo edit(@RequestBody Department department){
        if(teamDepartmentService.updateById(department)){
            return ResultUtils.success("成功");
        }
        return ResultUtils.error("失败");
    }




    /**
     * 根据ID获取科室详情
     * @param deptId 科室ID
     * @return ResultVo 科室实体
     */
    @GetMapping("/{deptId}")
    public ResultVo getDepartmentById(@PathVariable("deptId") Integer deptId) {
        Department department = teamDepartmentService.getById(deptId);
        if (department != null) {
            return ResultUtils.success("查询成功", department);
        }
        return ResultUtils.error("未找到指定科室");
    }

    /**
     * 检查科室名称是否唯一
     * @param deptName 科室名称
     * @param deptId   科室ID (编辑时可选)
     * @return ResultVo true表示已存在
     */
    @GetMapping("/checkDeptName")
    public ResultVo checkDeptName(@RequestParam("deptName") String deptName, @RequestParam(value = "deptId", required = false) Integer deptId) {
        boolean exists = teamDepartmentService.checkDeptName(deptName, deptId);
        return ResultUtils.success("查询成功", exists);
    }

    /**
     * 批量删除科室
     * @param deptIds 科室ID列表
     * @return ResultVo 操作结果
     */
    @DeleteMapping("/batch")
    public ResultVo batchDelete(@RequestBody List<Integer> deptIds) {
        if (teamDepartmentService.batchDelete(deptIds)) {
            return ResultUtils.success("批量删除成功");
        }
        return ResultUtils.error("批量删除失败");
    }

    /**
     * 根据科室名称精确查找
     * @param deptName 科室名称
     * @return ResultVo 查找到的科室
     */
    @GetMapping("/byName")
    public ResultVo getDepartmentByExactName(@RequestParam("deptName") String deptName) {
        Department department = teamDepartmentService.findByExactName(deptName);
        if (department != null) {
            return ResultUtils.success("查询成功", department);
        }
        return ResultUtils.error("未找到该科室");
    }

    /**
     * 查询排序号最大的科室
     * @return ResultVo 排序号最大的科室
     */
    @GetMapping("/maxOrder")
    public ResultVo getDepartmentWithMaxOrder() {
        Department department = teamDepartmentService.findDepartmentWithMaxOrderNum();
        if (department != null) {
            return ResultUtils.success("查询成功", department);
        }
        return ResultUtils.error("当前无科室数据");
    }

    // 删除科室
    @PreAuthorize("hasAuthority('sys:department:delete')")
    @DeleteMapping("/{deptId}")
    public ResultVo delete(@PathVariable("deptId") Long deptId){
        if(teamDepartmentService.removeById(deptId)){
            return ResultUtils.success("成功");
        }
        return ResultUtils.error("失败");
    }

    /**
     * 列表分页查询
     * @param parm 分页及查询参数
     * @return ResultVo 包含分页数据的列表
     */
    @GetMapping("/getList")
    public ResultVo getList(teamDepartmentPage parm){
        // 构造分页对象
        IPage<Department> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        // 构造查询条件
        QueryWrapper<Department> query = new QueryWrapper<>();

        // 科室名称模糊查询
        if(StringUtils.isNotEmpty(parm.getDeptName())){
            query.lambda().like(Department::getDeptName,parm.getDeptName());
        }

        // 按 order_num 字段降序排序
        query.lambda().orderByDesc(Department::getOrderNum);

        IPage<Department> list = teamDepartmentService.page(page, query);
        return ResultUtils.success("成功",list);
    }

    /**
     * 查询科室下拉列表数据
     * @return ResultVo 包含科室ID和名称的列表
     */
    @GetMapping("/getSelectDept")
    public ResultVo getSelectDept(){
        QueryWrapper<Department> query = new QueryWrapper<>();
        query.lambda().orderByDesc(Department::getOrderNum);
        List<Department> list = teamDepartmentService.list(query);

        // 封装为下拉框需要的格式
        List<teamDepartment> deptList = new ArrayList<>();
        if(list.size() > 0){
            for (int i=0;i<list.size();i++){
                teamDepartment dept = new teamDepartment();
                dept.setLabel(list.get(i).getDeptName());
                dept.setValue(list.get(i).getDeptId());
                deptList.add(dept);
            }

        }
        return ResultUtils.success("成功",deptList);
    }
}