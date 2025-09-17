package com.itmk.web.phone.entity;

import lombok.Data;

 
@Data
public class DoctorPage {
    private Integer deptId;
    //当前第几页
    private Long currentPage;
    //没有查询的条数
    private Long pageSize;
}
