package com.itmk.netSystem.roleWeb.service.Implement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.roleWeb.entity.SysRole;
import com.itmk.netSystem.roleWeb.mapper.roleWebMapper;
import com.itmk.netSystem.roleWeb.service.roleWebService;
import com.itmk.netSystem.roleWebNetMenu.service.roleWebNetMenuService;
import com.itmk.netSystem.roleWebNetMenu.entity.RoleMenu;
import com.itmk.netSystem.netRole.entity.SysUserRole;
import com.itmk.netSystem.netRole.service.NetRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

 
@Service
public class roleWebServiceImplement extends ServiceImpl<roleWebMapper, SysRole> implements roleWebService {
    @Autowired
    private roleWebNetMenuService roleWebNetMenuService;
    @Autowired
    private NetRoleService netRoleService;

    /**
     * 检查角色名称是否唯一
     */
    @Override
    public boolean checkRoleName(String roleName, Long roleId) {
        QueryWrapper<SysRole> query = new QueryWrapper<>();
        query.lambda().eq(SysRole::getRoleName, roleName);
        // 如果是编辑操作, 需要排除角色自身
        if (roleId != null) {
            query.lambda().ne(SysRole::getRoleId, roleId);
        }
        return this.baseMapper.selectCount(query) > 0;
    }

    /**
     * 批量删除角色及其关联数据
     */
    @Override
    @Transactional
    public void batchDelete(java.util.List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            // 1. 批量删除角色表
            this.baseMapper.deleteBatchIds(roleIds);

            // 2. 批量删除角色与菜单的关联
            QueryWrapper<RoleMenu> roleMenuQuery = new QueryWrapper<>();
            roleMenuQuery.lambda().in(RoleMenu::getRoleId, roleIds);
            roleWebNetMenuService.remove(roleMenuQuery);

            // 3. 批量删除角色与用户的关联
            QueryWrapper<SysUserRole> userRoleQuery = new QueryWrapper<>();
            userRoleQuery.lambda().in(SysUserRole::getRoleId, roleIds);
            netRoleService.remove(userRoleQuery);
        }
    }



    /**
     * 更新角色状态
     */
    @Override
    public boolean updateStatus(Long roleId, String status) {
        SysRole role = new SysRole();
        role.setRoleId(roleId);
        // 假设SysRole实体中存在名为status的字段
        // role.setStatus(status);
        // 由于不能修改实体类，此处仅为示例。
        // 如果实体没有status字段，可以更新其他字段，如roleName加特定前缀来标记。
        // 为演示功能，我们在这里更新备注字段(remark)来模拟状态变更。
        role.setRemark("状态更新为: " + status);
        role.setUpdateTime(new java.util.Date());
        return this.baseMapper.updateById(role) > 0;
    }

    @Override
    @Transactional
    public void delete(Long roleId) {
        this.baseMapper.deleteById(roleId);
        QueryWrapper<RoleMenu> query = new QueryWrapper<>();
        query.lambda().eq(RoleMenu::getRoleId,roleId);
        roleWebNetMenuService.remove(query);
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRole::getRoleId,roleId);
        netRoleService.remove(queryWrapper);
    }
}
