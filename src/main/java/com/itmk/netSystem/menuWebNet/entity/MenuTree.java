package com.itmk.netSystem.menuWebNet.entity;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 菜单树和路由数据组装工具类
public class MenuTree {

    /**
     * 构造用于前端路由的RouterVO列表
     * @param menuList 所有菜单列表
     * @param pid 父级ID
     * @return List<RouterVO> 路由列表
     */
    public static List<Router> makeRourer(List<SysMenu> menuList, Long pid) {
        // 构建存放路由数据的容器
        List<Router> list = new ArrayList<>();

        // 过滤出当前父级下的菜单
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null && item.getParentId().equals(pid))
                .forEach(item -> {
                    // 组装路由
                    Router router = new Router();
                    // 设置路由的名称和路径
                    router.setName(item.getName());
                    router.setPath(item.getPath());

                    // 递归设置子路由
                    List<Router> children = makeRourer(menuList, item.getMenuId());
                    router.setChildren(children);

                    // 处理一级菜单 (顶层)
                    if (item.getParentId() == 0L) {
                        router.setComponent("Layout"); // 一级菜单通常使用 Layout 布局

                        // 如果一级菜单是 '菜单' 类型 ('1')，需要特殊处理
                        if (item.getType().equals("1")) {
                            router.setRedirect(item.getPath());

                            // 构造子路由列表
                            List<Router> listChild = new ArrayList<>();
                            Router child = new Router();
                            child.setName(item.getName());
                            child.setPath(item.getPath());
                            child.setComponent(item.getUrl());

                            // 设置路由元数据 Meta
                            child.setMeta(child.new Meta(
                                    item.getTitle(),
                                    item.getIcon(),
                                    item.getCode().split(",")
                            ));
                            listChild.add(child);
                            router.setChildren(listChild);
                            router.setPath(item.getPath());
                            router.setName(item.getName());
                        }
                    } else {
                        // 非一级菜单，使用配置的URL作为组件
                        router.setComponent(item.getUrl());
                    }

                    // 设置路由元数据 Meta
                    router.setMeta(router.new Meta(
                            item.getTitle(),
                            item.getIcon(),
                            item.getCode().split(",")
                    ));
                    list.add(router);
                });
        return list;
    }

    /**
     * 组装菜单树结构 (用于权限分配等场景)
     * @param menuList 所有菜单列表
     * @param pid 父级ID
     * @return List<SysMenu> 菜单树列表
     */
    public static List<SysMenu> makeTree(List<SysMenu> menuList,Long pid){
        // 存放组装的树数据
        List<SysMenu> list = new ArrayList<>();

        // 过滤出当前父级下的菜单
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null && item.getParentId().equals(pid))
                .forEach(item ->{
                    SysMenu menu = new SysMenu();
                    BeanUtils.copyProperties(item,menu);
                    menu.setLabel(item.getTitle());
                    menu.setValue(item.getMenuId());

                    // 递归查找下级菜单
                    List<SysMenu> children = makeTree(menuList, item.getMenuId());
                    menu.setChildren(children);
                    list.add(menu);
                });
        return list;
    }
}