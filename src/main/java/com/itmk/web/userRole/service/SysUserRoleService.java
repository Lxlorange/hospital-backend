package com.itmk.web.userRole.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.userRole.entity.SysUserRole;

 
public interface SysUserRoleService extends IService<SysUserRole> {
    //保存角色
    void saveRole(SysUserRole sysUserRole);
}
