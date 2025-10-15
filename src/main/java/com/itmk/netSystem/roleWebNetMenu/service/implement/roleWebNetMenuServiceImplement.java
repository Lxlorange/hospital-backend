package com.itmk.netSystem.roleWebNetMenu.service.implement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.roleWebNetMenu.service.roleWebNetMenuService;
import com.itmk.netSystem.roleWebNetMenu.entity.RoleMenu;
import com.itmk.netSystem.roleWebNetMenu.entity.MenuNum;
import com.itmk.netSystem.roleWebNetMenu.mapper.roleWebNetMenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
 
@Service
public class roleWebNetMenuServiceImplement extends ServiceImpl<roleWebNetMenuMapper, RoleMenu> implements roleWebNetMenuService {


    @Override
    public java.util.List<Long> getMenuIdsByRoleId(Long roleId) {
        QueryWrapper<RoleMenu> query = new QueryWrapper<>();
        // 只查询 menu_id 字段，提高效率
        query.lambda().select(RoleMenu::getMenuId).eq(RoleMenu::getRoleId, roleId);
        java.util.List<RoleMenu> roleMenus = this.baseMapper.selectList(query);
        // 将查询结果从 List<RoleMenu> 转换为 List<Long>
        return roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    public java.util.List<Long> getRoleIdsByMenuId(Long menuId) {
        QueryWrapper<RoleMenu> query = new QueryWrapper<>();
        query.lambda().select(RoleMenu::getRoleId).eq(RoleMenu::getMenuId, menuId);
        java.util.List<RoleMenu> roleMenus = this.baseMapper.selectList(query);
        return roleMenus.stream()
                .map(RoleMenu::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteByRoleId(Long roleId) {
        QueryWrapper<RoleMenu> query = new QueryWrapper<>();
        query.lambda().eq(RoleMenu::getRoleId, roleId);
        return this.remove(query);
    }

    @Override
    public boolean deleteByMenuId(Long menuId) {
        QueryWrapper<RoleMenu> query = new QueryWrapper<>();
        query.lambda().eq(RoleMenu::getMenuId, menuId);
        return this.remove(query);
    }

    @Override
    public boolean addMenuToRole(Long roleId, Long menuId) {
        // 1. 检查该权限关联是否已存在
        QueryWrapper<RoleMenu> query = new QueryWrapper<>();
        query.lambda().eq(RoleMenu::getRoleId, roleId).eq(RoleMenu::getMenuId, menuId);
        if (this.baseMapper.selectCount(query) > 0) {
            return false; // 已存在，无需重复添加
        }

        // 2. 如果不存在，则插入新纪录
        RoleMenu roleMenu = new RoleMenu();
        roleMenu.setRoleId(roleId);
        roleMenu.setMenuId(menuId);
        return this.save(roleMenu);
    }
    @Override
    @Transactional
    public void saveRoleMenu(MenuNum parm) {
        QueryWrapper<RoleMenu> query = new QueryWrapper<>();
        query.lambda().eq(RoleMenu::getRoleId,parm.getRoleId());
        this.baseMapper.delete(query);
        this.baseMapper.saveRoleMenu(parm.getRoleId(),parm.getList());
    }
}
