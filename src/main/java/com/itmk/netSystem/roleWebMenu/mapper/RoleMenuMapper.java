package com.itmk.netSystem.roleWebMenu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.netSystem.roleWebMenu.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

 
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    //报错角色菜单
    boolean saveRoleMenu(@Param("roleId") Long roleId, @Param("menuIds")List<Long> menuIds);
}
