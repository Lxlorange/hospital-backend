package com.itmk.netSystem.userWeb.entity;

import lombok.Data;

 
@Data
public class SysUserPage {
     private String phone;
     private String nickName;
     private Long currentPage;
     private Long pageSize;
}
