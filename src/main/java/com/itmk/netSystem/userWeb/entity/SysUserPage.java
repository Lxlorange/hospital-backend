package com.itmk.netSystem.userWeb.entity;

import lombok.Data;

 
@Data
public class SysUserPage {
     private String phone;
     private String nickName;
      //当前第几页
    private Long currentPage;
    //没有查询的条数
    private Long pageSize;
}
