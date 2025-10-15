package com.itmk.netSystem.userWeb.entity;

import lombok.Data;

 
@Data
public class userWebPage {
     private String phone;
     private String nickName;
     private Long currentPage;
     private Long pageSize;
}
