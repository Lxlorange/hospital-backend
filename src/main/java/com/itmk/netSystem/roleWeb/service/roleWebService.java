package com.itmk.netSystem.roleWeb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.roleWeb.entity.SysRole;

 
public interface roleWebService extends IService<SysRole> {
    /**
     * 检查角色名称是否唯一
     * @param roleName 角色名称
     * @param roleId   角色ID (编辑时传入, 用于排除自身)
     * @return boolean true表示已存在, false表示不存在
     */
    boolean checkRoleName(String roleName, Long roleId);

    /**
     * 批量删除角色
     * @param roleIds 角色ID列表
     */
    void batchDelete(java.util.List<Long> roleIds);



    /**
     * 更新角色状态 (例如：启用/禁用)
     * @param roleId 角色ID
     * @param status 新的状态
     * @return boolean 是否更新成功
     */
    boolean updateStatus(Long roleId, String status);
    void delete(Long roleId);
}
