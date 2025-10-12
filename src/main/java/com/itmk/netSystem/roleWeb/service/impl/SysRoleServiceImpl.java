package com.itmk.netSystem.roleWeb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.roleWeb.entity.SysRole;
import com.itmk.netSystem.roleWeb.mapper.SysRoleMapper;
import com.itmk.netSystem.roleWeb.service.SysRoleService;
import com.itmk.netSystem.roleWebMenu.RoleMenu.RoleMenuService;
import com.itmk.netSystem.roleWebMenu.entity.RoleMenu;
import com.itmk.netSystem.userRole.entity.SysUserRole;
import com.itmk.netSystem.userRole.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

 
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Override
    @Transactional
    public void delete(Long roleId) {
        //删除角色
        this.baseMapper.deleteById(roleId);
        //删除角色对应的菜单
        QueryWrapper<RoleMenu> query = new QueryWrapper<>();
        query.lambda().eq(RoleMenu::getRoleId,roleId);
        roleMenuService.remove(query);
        //删除角色用户表数据
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRole::getRoleId,roleId);
        sysUserRoleService.remove(queryWrapper);
    }
}
