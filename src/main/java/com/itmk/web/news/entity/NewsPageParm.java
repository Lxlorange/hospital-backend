package com.itmk.web.news.entity;
import lombok.Data;

 
@Data
public class NewsPageParm {
    //当前第几页
    private Long currentPage;
    //没有查询的条数
    private Long pageSize;
    //查询关键字
    private String keywords;
}