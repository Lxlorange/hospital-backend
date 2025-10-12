package com.itmk.netSystem.userWeb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.menuWeb.entity.AssignTreeParm;
import com.itmk.netSystem.menuWeb.entity.AssignTreeVo;
import com.itmk.netSystem.userWeb.entity.SysUser;

 
public interface SysUserService extends IService<SysUser> {
    //新增
    void saveUser(SysUser sysUser);
    //编辑
    void editUser(SysUser sysUser);
    //删除用户
    void deleteUser(Long userId);
    //查询菜单树
    AssignTreeVo getAssignTree(AssignTreeParm parm);
    //根据用户名查询
    SysUser loadUser(String username);
}
