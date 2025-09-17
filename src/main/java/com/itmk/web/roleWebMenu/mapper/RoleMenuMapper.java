package com.itmk.web.roleWebMenu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.web.roleWebMenu.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

 
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    //报错角色菜单
    boolean saveRoleMenu(@Param("roleId") Long roleId, @Param("menuIds")List<Long> menuIds);
}
