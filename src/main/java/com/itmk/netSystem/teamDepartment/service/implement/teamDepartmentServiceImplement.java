package com.itmk.netSystem.teamDepartment.service.implement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.teamDepartment.mapper.teamDepartmentMapper;
import com.itmk.netSystem.teamDepartment.service.teamDepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;
 
@Service
public class teamDepartmentServiceImplement extends ServiceImpl<teamDepartmentMapper, Department> implements teamDepartmentService {


    @Override
    public boolean checkDeptName(String deptName, Integer deptId) {
        QueryWrapper<Department> query = new QueryWrapper<>();
        query.lambda().eq(Department::getDeptName, deptName);
        if (deptId != null) {
            query.lambda().ne(Department::getDeptId, deptId);
        }
        return this.baseMapper.selectCount(query) > 0;
    }

    @Override
    public boolean batchDelete(List<Integer> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return false;
        }
        // TODO: 在实际业务中，删除前应检查科室下是否有未转移的医生或排班
        return this.removeByIds(deptIds);
    }

    @Override
    public Department findByExactName(String deptName) {
        QueryWrapper<Department> query = new QueryWrapper<>();
        query.lambda().eq(Department::getDeptName, deptName);
        return this.baseMapper.selectOne(query);
    }

    @Override
    public Department findDepartmentWithMaxOrderNum() {
        QueryWrapper<Department> query = new QueryWrapper<>();
        query.lambda().orderByDesc(Department::getOrderNum).last("LIMIT 1");
        return this.baseMapper.selectOne(query);
    }
}
