package com.itmk.web.patientOrder.entity;

import lombok.Data;

 
@Data
public class MakeOrderVisitPageParm {
    //当前第几页
    private Long currentPage;
    //没有查询的条数
    private Long pageSize;
    /** 预约人id */
    private Integer userId;
    private Long doctorId;
    private String name;
    private String timesArea;
}
