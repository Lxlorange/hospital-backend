package com.itmk.netSystem.roleWebNetMenu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.roleWebNetMenu.entity.RoleMenu;
import com.itmk.netSystem.roleWebNetMenu.entity.SaveMenuParm;

 
public interface roleWebNetMenuService extends IService<RoleMenu> {
    void saveRoleMenu(SaveMenuParm parm);
    /**
     * 根据角色ID查询其拥有的所有菜单ID列表
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    java.util.List<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * 根据菜单ID查询拥有该菜单的所有角色ID列表
     * @param menuId 菜单ID
     * @return 角色ID列表
     */
    java.util.List<Long> getRoleIdsByMenuId(Long menuId);

    /**
     * 根据角色ID删除该角色的所有菜单关联
     * @param roleId 角色ID
     * @return 是否删除成功
     */
    boolean deleteByRoleId(Long roleId);

    /**
     * 根据菜单ID删除该菜单的所有角色关联
     * @param menuId 菜单ID
     * @return 是否删除成功
     */
    boolean deleteByMenuId(Long menuId);

    /**
     * 为角色添加单个菜单权限
     * @param roleId 角色ID
     * @param menuId 菜单ID
     * @return 是否添加成功 (如果已存在则返回false)
     */
    boolean addMenuToRole(Long roleId, Long menuId);
}
