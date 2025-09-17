package com.itmk.web.roleWeb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.roleWeb.entity.SysRole;

 
public interface SysRoleService extends IService<SysRole> {
    //删除角色
    void delete(Long roleId);
}
