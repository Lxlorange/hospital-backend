package com.itmk.web.userPhone.entity;

import lombok.Data;

 
@Data
public class WxUserPageParm {
    //当前第几页
    private Long currentPage;
    //没有查询的条数
    private Long pageSize;
    private String name;
}