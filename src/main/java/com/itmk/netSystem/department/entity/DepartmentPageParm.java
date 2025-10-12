package com.itmk.netSystem.department.entity;

import lombok.Data;

 
@Data
public class DepartmentPageParm {
    //当前第几页
    private Long currentPage;
    //查询的条数
    private Long pageSize;
    //根据名称搜索
    private String deptName;
}
