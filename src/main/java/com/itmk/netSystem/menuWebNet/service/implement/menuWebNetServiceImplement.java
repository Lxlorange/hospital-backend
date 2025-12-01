package com.itmk.netSystem.menuWebNet.service.implement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.menuWebNet.entity.MenuTree;
import com.itmk.netSystem.menuWebNet.entity.SysMenu;
import com.itmk.netSystem.menuWebNet.mapper.menuWebNetMapper;
import com.itmk.netSystem.menuWebNet.service.menuWebNetService;
import com.itmk.netSystem.userWeb.service.userWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
// 系统菜单服务实现类
@Service
public class menuWebNetServiceImplement extends ServiceImpl<menuWebNetMapper, SysMenu> implements menuWebNetService {
    @Autowired
    private userWebService userWebService; // 用户服务（虽然在此类中未使用，但保留）

    /**
     * 根据用户ID查询其拥有的菜单列表
     * @param userId 用户ID
     * @return List<SysMenu> 菜单列表
     */
    @Override
    public List<SysMenu> getMenuByUserId(Long userId) {
        return this.baseMapper.getMenuByUserId(userId);
    }



    @Override
    public boolean checkPathExists(String path, Long parentId, Long menuId) {
        QueryWrapper<SysMenu> query = new QueryWrapper<>();
        query.lambda()
                .eq(SysMenu::getPath, path)
                .eq(SysMenu::getParentId, parentId);
        if (menuId != null) {
            query.lambda().ne(SysMenu::getMenuId, menuId);
        }
        return this.baseMapper.selectCount(query) > 0;
    }

    @Override
    public List<SysMenu> findMenusByType(String type) {
        QueryWrapper<SysMenu> query = new QueryWrapper<>();
        query.lambda().eq(SysMenu::getType, type).orderByAsc(SysMenu::getOrderNum);
        return this.list(query);
    }

    @Override
    public List<SysMenu> getParentPath(Long menuId) {
        List<SysMenu> pathList = this.baseMapper.getParentPath(menuId);
        // SQL递归查询出的路径通常是倒序的(子->父)，需要反转为(父->子)
        Collections.reverse(pathList);
        return pathList;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public boolean updateOrderBatch(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return true;
        }
        return this.baseMapper.updateOrderBatch(menus) > 0;
    }

    @Override
    public List<SysMenu> getAllChildren(Long menuId) {
        return this.baseMapper.getAllChildren(menuId);
    }

    /**
     * 根据角色ID查询其拥有的菜单列表
     * @param roleId 角色ID
     * @return List<SysMenu> 菜单列表
     */
    @Override
    public List<SysMenu> getMenuByRoleId(Long roleId) {
        return this.baseMapper.getMenuByRoleId(roleId);
    }

    /**
     * 获取用于选择父级菜单的菜单树结构 (包含菜单和目录)
     * @return List<SysMenu> 菜单树列表
     */
    @Override
    public List<SysMenu> getParent() {
        // 筛选类型为 '0' (目录) 和 '1' (菜单)
        String[] type = {"0","1"};
        List<String> strings = Arrays.asList(type);
        QueryWrapper<SysMenu> query = new QueryWrapper<>();
        query.lambda().in(SysMenu::getType,strings).orderByAsc(SysMenu::getOrderNum);

        // 查询符合条件的菜单列表
        List<SysMenu> menuList = this.baseMapper.selectList(query);

        // 构造虚拟的顶级菜单节点
        SysMenu menu = new SysMenu();
        menu.setTitle("顶级菜单");
        menu.setLabel("顶级菜单");
        menu.setParentId(-1L);
        menu.setMenuId(0L);
        menu.setValue(0L);
        menuList.add(menu);

        // 组装菜单树结构
        List<SysMenu> tree = MenuTree.makeTree(menuList, -1L);
        return tree;
    }


}