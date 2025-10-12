package com.itmk.netSystem.roleWebMenu.RoleMenu;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.roleWebMenu.entity.RoleMenu;
import com.itmk.netSystem.roleWebMenu.entity.SaveMenuParm;

 
public interface RoleMenuService extends IService<RoleMenu> {
    void saveRoleMenu(SaveMenuParm parm);
}
