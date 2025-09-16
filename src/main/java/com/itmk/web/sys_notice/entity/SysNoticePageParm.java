package com.itmk.web.sys_notice.entity;

import lombok.Data;

 
@Data
public class SysNoticePageParm {
    //当前第几页
    private Long currentPage;
    //每页查询的条数
    private Long pageSize;
    //查询关键字
    private String noticeTitle;
}