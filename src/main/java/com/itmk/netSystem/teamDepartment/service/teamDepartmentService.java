package com.itmk.netSystem.teamDepartment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.teamDepartment.entity.Department;

 
public interface teamDepartmentService extends IService<Department> {
    /**
     * 检查科室名称是否唯一
     * @param deptName 科室名称
     * @param deptId   科室ID (编辑时传入, 用于排除自身)
     * @return boolean true表示已存在, false表示不存在
     */
    boolean checkDeptName(String deptName, Integer deptId);

    /**
     * 批量删除科室
     * @param deptIds 科室ID列表
     * @return boolean 是否删除成功
     */
    boolean batchDelete(java.util.List<Integer> deptIds);

    /**
     * 根据科室名称精确查找科室
     * @param deptName 科室名称
     * @return Department 实体，若未找到则返回null
     */
    Department findByExactName(String deptName);

    /**
     * 查询排序号(orderNum)最大的科室
     * @return Department 实体，若无数据则返回null
     */
    Department findDepartmentWithMaxOrderNum();
}
