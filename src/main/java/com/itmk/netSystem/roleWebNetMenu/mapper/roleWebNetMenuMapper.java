package com.itmk.netSystem.roleWebNetMenu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.netSystem.roleWebNetMenu.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

 
public interface roleWebNetMenuMapper extends BaseMapper<RoleMenu> {
    /**
     * 根据角色ID列表批量删除关联的菜单权限.
     * @param roleIds 角色ID列表
     * @return 影响的行数
     */
    int deleteByRoleIds(@Param("roleIds") java.util.List<Long> roleIds);

    /**
     * 根据菜单ID列表批量删除其所有角色关联.
     * @param menuIds 菜单ID列表
     * @return 影响的行数
     */
    int deleteByMenuIds(@Param("menuIds") java.util.List<Long> menuIds);

    /**
     * 查询拥有指定所有菜单权限的角色ID列表.
     * <p>
     * 例如，找出同时拥有 "用户新增" 和 "用户编辑" 权限的角色.
     * @param menuIds 必须拥有的菜单ID列表
     * @return 符合条件的角色ID列表
     */
    java.util.List<Long> findRoleIdsWithAllMenus(@Param("menuIds") java.util.List<Long> menuIds);

    /**
     * 统计每个角色拥有的菜单权限数量.
     * @return 一个Map列表，每个Map包含 "roleId" 和 "menuCount"
     */
    java.util.List<java.util.Map<String, Object>> countMenusPerRole();

    /**
     * 查找没有任何菜单权限的空角色ID列表.
     * @return 空角色的ID列表
     */
    java.util.List<Long> findRolesWithNoMenus();
    boolean saveRoleMenu(@Param("roleId") Long roleId, @Param("menuIds")List<Long> menuIds);
}
