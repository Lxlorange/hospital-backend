package com.itmk.web.schedule.entity;

import lombok.Data;

 
@Data
public class ScheduleDetailListParm {
    //当前第几页
    private Long currentPage;
    //查询的条数
    private Long pageSize;
    //根据名称搜索
    private String doctorName;
}
