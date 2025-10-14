package com.itmk.netSystem.userWeb.entity;

import lombok.Data;


@Data
public class UserInfo {
    private Long userId;
    private String name;
    private Object[] permissons;
}
