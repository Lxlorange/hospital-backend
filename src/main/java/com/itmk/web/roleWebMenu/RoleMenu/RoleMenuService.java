package com.itmk.web.roleWebMenu.RoleMenu;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.roleWebMenu.entity.RoleMenu;
import com.itmk.web.roleWebMenu.entity.SaveMenuParm;

 
public interface RoleMenuService extends IService<RoleMenu> {
    void saveRoleMenu(SaveMenuParm parm);
}
