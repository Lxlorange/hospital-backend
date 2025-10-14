package com.itmk.netSystem.userWeb.entity;

import lombok.Data;

 
@Data
public class LoginVo {
    private Long userId;
    private String nickName;
    private String token;
    private String type;
}