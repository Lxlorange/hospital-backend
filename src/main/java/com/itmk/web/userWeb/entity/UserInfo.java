package com.itmk.web.userWeb.entity;

import lombok.Data;

/**
 * 返回用户的信息
 *  
 *  
 */
@Data
public class UserInfo {
    private Long userId;
    private String name;
    private Object[] permissons;
}
