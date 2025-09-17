package com.itmk.web.phone.entity;

import lombok.Data;

 
@Data
public class UpdatePasswordParm {
    private Integer userId;
    private String oldPassword;
    private String password;
}
