package com.itmk.netSystem.userWeb.entity;

import lombok.Data;

 
@Data
public class resetPasswordParm {
    private Long userId;
    private String oldPassword;
    private String password;
}
