package com.itmk.web.sys_role.entity;

import lombok.Data;

 
@Data
public class RoleParm {
    //当前第几页
    private Long currentPage;
    //没有查询的条数
    private Long pageSize;
    //角色名称
    private String roleName;
}