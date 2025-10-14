package com.itmk.netSystem.userWeb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.menuWebNet.entity.AssignTreeParm;
import com.itmk.netSystem.menuWebNet.entity.AssignTreeVo;
import com.itmk.netSystem.userWeb.entity.SysUser;

import java.util.List;

// 系统用户服务接口，继承自MyBatis Plus的IService
public interface userWebService extends IService<SysUser> {

    /**
     * 编辑用户信息
     * @param sysUser 待编辑的用户实体
     */
    void editUser(SysUser sysUser);

    /**
     * 新增用户
     * @param sysUser 待保存的用户实体
     */
    void saveUser(SysUser sysUser);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return SysUser 用户实体
     */

    /**
     * 检查用户名是否已存在
     * @param username 待检查的用户名
     * @return boolean true表示已存在，false表示不存在
     */
    boolean checkUsername(String username);

    /**
     * 获取当前登录用户的个人资料
     * @param userId 用户ID
     * @return SysUser 用户实体，密码等敏感信息已脱敏
     */
    SysUser getUserProfile(Long userId);

    /**
     * 更新当前登录用户的个人资料
     * @param sysUser 包含新信息的用户实体
     * @return boolean 是否更新成功
     */
    boolean updateUserProfile(SysUser sysUser);


    /**
     * 删除用户
     * @param userId 待删除的用户ID
     */
    void deleteUser(Long userId);

    /**
     * 查询分配权限的菜单树结构
     * @param parm 查询参数，如用户ID或角色ID
     * @return AssignTreeVo 包含菜单树数据的VO
     */
    AssignTreeVo getAssignTree(AssignTreeParm parm);

    /**
     * 批量删除用户
     * @param userIds 用户ID列表
     */
    void batchDeleteUsers(List<Long> userIds);

    /**
     * 根据邮箱查找用户
     * @param email 邮箱地址
     * @return SysUser 匹配的用户实体
     */
    SysUser findByEmail(String email);

    SysUser loadUser(String username);


}