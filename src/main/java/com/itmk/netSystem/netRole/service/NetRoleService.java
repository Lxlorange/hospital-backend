package com.itmk.netSystem.netRole.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.netRole.entity.SysUserRole;

 
public interface NetRoleService extends IService<SysUserRole> {
    /**
     * 根据用户ID查询其拥有的所有角色ID列表
     * @param userId 用户ID
     * @return 角色ID列表
     */
    java.util.List<Long> findRoleIdsByUserId(Long userId);

    /**
     * 根据角色ID查询拥有该角色的所有用户ID列表
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    java.util.List<Long> findUserIdsByRoleId(Long roleId);

    /**
     * 根据用户ID删除该用户的所有角色关联
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteByUserId(Long userId);

    /**
     * 为用户重新分配角色
     * @param userId 用户ID
     * @param newRoleIds 新的角色ID列表
     */
    void reassignRolesForUser(Long userId, java.util.List<Long> newRoleIds);

    /**
     * 检查用户是否拥有特定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return boolean true表示拥有，false表示没有
     */
    boolean checkUserHasRole(Long userId, Long roleId);

    void saveRole(SysUserRole sysUserRole);
}
