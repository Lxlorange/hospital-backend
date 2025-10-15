package com.itmk.netSystem.userWeb.service.implement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.menuWebNet.entity.AssignTreeNum;
import com.itmk.netSystem.menuWebNet.entity.AssignTree;
import com.itmk.netSystem.menuWebNet.entity.MenuTree;
import com.itmk.netSystem.menuWebNet.entity.SysMenu;
import com.itmk.netSystem.menuWebNet.service.menuWebNetService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.mapper.userWebMapper;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.netSystem.netRole.entity.SysUserRole;
import com.itmk.netSystem.netRole.service.NetRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 用户服务实现类
@Service
public class userWebServiceImplement extends ServiceImpl<userWebMapper, SysUser> implements userWebService {
    @Autowired
    private NetRoleService netRoleService; // 用户角色服务
    @Autowired
    private menuWebNetService menuWebNetService; // 系统菜单服务

    /**
     * 编辑用户信息及更新用户角色
     * @param sysUser 待编辑的用户实体
     */
    @Override
    @Transactional
    public void editUser(SysUser sysUser) {
        // 编辑用户信息
        int i = this.baseMapper.updateById(sysUser);

        if(i > 0){
            // 解析角色ID字符串为数组
            String[] split = sysUser.getRoleId().split(",");

            // 删除用户原来的角色关联
            QueryWrapper<SysUserRole> query = new QueryWrapper<>();
            query.lambda().eq(SysUserRole::getUserId,sysUser.getUserId());
            netRoleService.remove(query);

            // 重新插入新的角色关联
            if(split.length > 0){
                List<SysUserRole> roles = new ArrayList<>();
                for (int j = 0; j < split.length; j++) {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(sysUser.getUserId());
                    userRole.setRoleId(Long.parseLong(split[j]));
                    roles.add(userRole);
                }
                // 批量保存到用户角色表
                netRoleService.saveBatch(roles);
            }
        }
    }

    /**
     * 检查用户名是否已存在
     */
    @Override
    public boolean checkUsername(String username) {
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getUsername, username);
        return this.baseMapper.selectCount(query) > 0;
    }

    /**
     * 获取当前登录用户的个人资料
     */
    @Override
    public SysUser getUserProfile(Long userId) {
        SysUser user = this.baseMapper.selectById(userId);
        if (user != null) {
            // 脱敏处理，隐藏密码
            user.setPassword(null);
        }
        return user;
    }

    /**
     * 更新当前登录用户的个人资料
     */
    @Override
    public boolean updateUserProfile(SysUser sysUser) {
        // 出于安全考虑，只允许用户修改部分个人信息
        SysUser userToUpdate = new SysUser();
        userToUpdate.setUserId(sysUser.getUserId());
        userToUpdate.setNickName(sysUser.getNickName());
        userToUpdate.setPhone(sysUser.getPhone());
        userToUpdate.setEmail(sysUser.getEmail());
        userToUpdate.setSex(sysUser.getSex());
        userToUpdate.setUpdateTime(new java.util.Date());

        return this.baseMapper.updateById(userToUpdate) > 0;
    }

    /**
     * 批量删除用户
     */
    @Override
    @Transactional
    public void batchDeleteUsers(List<Long> userIds) {
        if(userIds != null && !userIds.isEmpty()){
            // 1. 批量删除用户与角色的关联
            QueryWrapper<SysUserRole> query = new QueryWrapper<>();
            query.lambda().in(SysUserRole::getUserId, userIds);
            netRoleService.remove(query);

            // 2. 批量删除用户
            this.baseMapper.deleteBatchIds(userIds);
        }
    }

    /**
     * 根据邮箱查找用户
     */
    @Override
    public SysUser findByEmail(String email) {
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getEmail, email);
        return this.baseMapper.selectOne(query);
    }

    /**
     * 新增用户及设置用户角色
     * @param sysUser 待保存的用户实体
     */
    @Transactional
    @Override
    public void saveUser(SysUser sysUser) {
        // 插入用户信息
        int i = this.baseMapper.insert(sysUser);

        if(i > 0){
            // 解析角色ID字符串为数组
            String[] split = sysUser.getRoleId().split(",");

            if(split.length > 0){
                List<SysUserRole> roles = new ArrayList<>();
                for (int j = 0; j < split.length; j++) {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(sysUser.getUserId());
                    userRole.setRoleId(Long.parseLong(split[j]));
                    roles.add(userRole);
                }
                // 保存到用户角色表
                netRoleService.saveBatch(roles);
            }
        }
    }

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return SysUser 用户实体
     */
    @Override
    public SysUser loadUser(String username) {
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getUsername,username);
        // 根据用户名查询单个用户
        SysUser user = this.baseMapper.selectOne(query);
        return user;
    }

    /**
     * 查询分配权限的菜单树结构
     * @param parm 查询参数，如用户ID和角色ID
     * @return AssignTreeVo 包含菜单树数据的VO
     */
    @Override
    public AssignTree getAssignTree(AssignTreeNum parm) {
        // 查询用户的信息
        SysUser user = this.baseMapper.selectById(parm.getUserId());
        List<SysMenu> menuList = null;

        // 判断是否是超级管理员
        if(StringUtils.isNotEmpty(user.getIsAdmin()) && "1".equals(user.getIsAdmin())){
            // 超级管理员，查询所有菜单
            menuList = menuWebNetService.list();
        }else{
            // 普通用户，查询用户拥有的菜单
            menuList = menuWebNetService.getMenuByUserId(parm.getUserId());
        }

        // 组装菜单树结构
        List<SysMenu> makeTree = MenuTree.makeTree(menuList, 0L);

        // 查询角色原来的菜单权限
        List<SysMenu> roleList = menuWebNetService.getMenuByRoleId(parm.getRoleId());
        List<Long> ids = new ArrayList<>();

        // 提取角色已有的菜单ID
        Optional.ofNullable(roleList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null)
                .forEach(item ->{
                    ids.add(item.getMenuId());
                });

        // 组装返回数据
        AssignTree vo = new AssignTree();
        vo.setCheckList(ids.toArray()); // 角色已拥有的菜单ID列表
        vo.setMenuList(makeTree); // 完整的菜单树结构
        return vo;
    }


    /**
     * 删除用户及其关联的角色
     * @param userId 待删除的用户ID
     */
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        // 删除用户
        int i = this.baseMapper.deleteById(userId);

        if(i > 0){
            // 删除用户关联的角色
            QueryWrapper<SysUserRole> query = new QueryWrapper<>();
            query.lambda().eq(SysUserRole::getUserId,userId);
            netRoleService.remove(query);
        }
    }


}