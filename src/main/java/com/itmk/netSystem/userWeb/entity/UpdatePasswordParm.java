package com.itmk.netSystem.userWeb.entity;

import lombok.Data;

 
@Data
public class UpdatePasswordParm {
    private Long userId;
    private String oldPassword;
    private String password;
}
