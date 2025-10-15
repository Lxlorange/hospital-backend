package com.itmk.netSystem.teamDepartment.entity;

import lombok.Data;

 
@Data
public class teamDepartmentPage {
    public teamDepartmentPage(Long currentPage, Long pageSize, String deptName) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.deptName = deptName;
    }

    public teamDepartmentPage() {
    }

    private Long currentPage;

    private Long pageSize;

    private String deptName;
}
