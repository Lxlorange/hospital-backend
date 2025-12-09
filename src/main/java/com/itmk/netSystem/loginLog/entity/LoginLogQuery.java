package com.itmk.netSystem.loginLog.entity;

import lombok.Data;

@Data
public class LoginLogQuery {
    private Long currentPage;
    private Long pageSize;
    private String name;
}