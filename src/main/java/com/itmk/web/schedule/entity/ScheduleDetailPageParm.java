package com.itmk.web.schedule.entity;
import lombok.Data;

 
@Data
public class ScheduleDetailPageParm {
    //当前第几页
    private Long currentPage;
    //没有查询的条数
    private Long pageSize;
    //医生姓名
    private String doctorName;
}