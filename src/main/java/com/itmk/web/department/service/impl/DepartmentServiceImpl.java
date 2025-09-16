package com.itmk.web.department.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.department.entity.Department;
import com.itmk.web.department.mapper.DepartmentMapper;
import com.itmk.web.department.service.DepartmentService;
import org.springframework.stereotype.Service;

 
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
}
