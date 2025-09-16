package com.itmk.web.wxapi.entity;

import lombok.Data;

 
@Data
public class UpdatePasswordParm {
    private Integer userId;
    private String oldPassword;
    private String password;
}
