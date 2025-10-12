package com.itmk.netSystem.userWeb.entity;

import lombok.Data;

 
@Data
public class LoginParm {
    private String username;
    private String password;
    private String code;
}