package com.itmk.netSystem.menuWebNet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.menuWebNet.entity.SysMenu;

import java.util.List;

 
public interface menuWebNetService extends IService<SysMenu> {
    List<SysMenu> getParent();
    List<SysMenu> getMenuByUserId(Long userId);
    /**
     * 检查菜单路径(path)在同一父目录下是否唯一
     * @param path     菜单路径
     * @param parentId 父菜单ID
     * @param menuId   当前菜单ID (编辑时用于排除自身)
     * @return boolean true表示已存在
     */
    boolean checkPathExists(String path, Long parentId, Long menuId);

    /**
     * 根据类型查询菜单列表 (0-目录, 1-菜单, 2-按钮)
     * @param type 菜单类型
     * @return 菜单列表
     */
    List<SysMenu> findMenusByType(String type);

    /**
     * 获取指定菜单的所有父级菜单（面包屑）
     * @param menuId 当前菜单ID
     * @return 从根到当前的菜单路径列表
     */
    List<SysMenu> getParentPath(Long menuId);

    /**
     * 批量更新菜单排序
     * @param menus 包含ID和新排序号的菜单列表
     * @return 是否更新成功
     */
    boolean updateOrderBatch(List<SysMenu> menus);

    /**
     * 获取指定菜单的所有层级子菜单
     * @param menuId 父菜单ID
     * @return 所有子孙菜单列表
     */
    List<SysMenu> getAllChildren(Long menuId);
    List<SysMenu> getMenuByRoleId(Long roleId);

}
