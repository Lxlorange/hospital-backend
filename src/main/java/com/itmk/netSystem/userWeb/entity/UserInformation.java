package com.itmk.netSystem.userWeb.entity;

import lombok.Data;


@Data
public class UserInformation {
    private Long userId;
    private String name;
    private Object[] permissons;
}
