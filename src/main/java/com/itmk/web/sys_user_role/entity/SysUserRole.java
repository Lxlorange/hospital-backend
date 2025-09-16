package com.itmk.web.sys_user_role.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

 
@Data
public class SysUserRole {
    @TableId(type = IdType.AUTO)
    private Long userRoleId;
    private Long userId;
    private Long roleId;
}
