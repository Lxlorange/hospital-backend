package com.itmk.web.plat.entity;

import lombok.Data;

 
@Data
public class TotalVo {
    private long departmentCount;
    private long sysUserCount;
    private long wxUserCount;
    private long visitCount;
}