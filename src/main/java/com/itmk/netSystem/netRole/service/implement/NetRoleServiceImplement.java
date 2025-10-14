package com.itmk.netSystem.netRole.service.implement;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.netRole.entity.SysUserRole;
import com.itmk.netSystem.netRole.mapper.NetRoleMapper;
import com.itmk.netSystem.netRole.service.NetRoleService;
import org.springframework.stereotype.Service;

 
@Service
public class NetRoleServiceImplement extends ServiceImpl<NetRoleMapper, SysUserRole> implements NetRoleService {
    /**
     * 根据用户ID查询其拥有的所有角色ID列表
     */
    @Override
    public java.util.List<Long> findRoleIdsByUserId(Long userId) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUserRole> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRole::getUserId, userId);
        java.util.List<SysUserRole> userRoles = this.baseMapper.selectList(queryWrapper);
        return userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据角色ID查询拥有该角色的所有用户ID列表
     */
    @Override
    public java.util.List<Long> findUserIdsByRoleId(Long roleId) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUserRole> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRole::getRoleId, roleId);
        java.util.List<SysUserRole> userRoles = this.baseMapper.selectList(queryWrapper);
        return userRoles.stream()
                .map(SysUserRole::getUserId)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据用户ID删除该用户的所有角色关联
     */
    @Override
    public boolean deleteByUserId(Long userId) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUserRole> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRole::getUserId, userId);
        return this.remove(queryWrapper);
    }

    /**
     * 为用户重新分配角色（先删后增）
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public void reassignRolesForUser(Long userId, java.util.List<Long> newRoleIds) {
        // 1. 删除该用户的所有旧角色
        deleteByUserId(userId);

        // 2. 如果新的角色列表不为空，则批量插入新角色
        if (newRoleIds != null && !newRoleIds.isEmpty()) {
            java.util.List<SysUserRole> userRoleList = newRoleIds.stream().map(roleId -> {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                return userRole;
            }).collect(java.util.stream.Collectors.toList());
            this.saveBatch(userRoleList);
        }
    }

    /**
     * 检查用户是否拥有特定角色
     */
    @Override
    public boolean checkUserHasRole(Long userId, Long roleId) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUserRole> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getRoleId, roleId);
        return this.baseMapper.selectCount(queryWrapper) > 0;
    }
    @Override
    public void saveRole(SysUserRole sysUserRole) {

    }
}
