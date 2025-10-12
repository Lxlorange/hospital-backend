package com.itmk.netSystem.department.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.department.entity.Department;
import com.itmk.netSystem.department.mapper.DepartmentMapper;
import com.itmk.netSystem.department.service.DepartmentService;
import org.springframework.stereotype.Service;

 
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
}
