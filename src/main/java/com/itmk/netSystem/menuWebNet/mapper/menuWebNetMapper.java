package com.itmk.netSystem.menuWebNet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.netSystem.menuWebNet.entity.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

 
public interface menuWebNetMapper extends BaseMapper<SysMenu> {
    /**
     * 递归查询指定菜单ID下的所有子菜单（包含孙菜单等）
     * @param menuId 父菜单ID
     * @return 所有子菜单列表
     */
    List<SysMenu> getAllChildren(@Param("menuId") Long menuId);

    /**
     * 递归查询指定菜单ID的所有父级菜单（面包屑导航）
     * @param menuId 当前菜单ID
     * @return 从根到当前菜单的路径列表
     */
    List<SysMenu> getParentPath(@Param("menuId") Long menuId);

    /**
     * 批量更新菜单的排序号
     * @param menus 包含 menuId 和新 orderNum 的菜单对象列表
     * @return 影响的行数
     */
    int updateOrderBatch(@Param("menus") List<SysMenu> menus);
    List<SysMenu> getMenuByUserId(@Param("userId") Long userId);
    List<SysMenu> getMenuByRoleId(@Param("roleId") Long roleId);
}
