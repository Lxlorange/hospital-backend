package com.itmk.netSystem.teamDepartment.entity;

import lombok.Data;

 
@Data
public class DepartmentPageParm {
    public DepartmentPageParm(Long currentPage, Long pageSize, String deptName) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.deptName = deptName;
    }

    public DepartmentPageParm() {
    }

    private Long currentPage;

    private Long pageSize;

    private String deptName;
}
