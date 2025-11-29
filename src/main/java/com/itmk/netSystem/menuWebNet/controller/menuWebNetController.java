package com.itmk.netSystem.menuWebNet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.netSystem.menuWebNet.entity.MenuTree;
import com.itmk.netSystem.menuWebNet.entity.Router;
import com.itmk.netSystem.menuWebNet.entity.SysMenu;
import com.itmk.netSystem.menuWebNet.service.menuWebNetService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 系统菜单管理控制器
@RequestMapping("/api/sysMenu")
@RestController
public class menuWebNetController {
    @Autowired
    private menuWebNetService menuWebNetService; // 系统菜单服务
    @Autowired
    private userWebService userWebService; // 系统用户服务

    /**
     * 新增菜单
     */
    @PreAuthorize("hasAuthority('sys:menu:add')")
    @PostMapping
    public ResultVo add(@RequestBody SysMenu sysMenu){
        sysMenu.setCreateTime(new Date());
        if(menuWebNetService.save(sysMenu)){
            return ResultUtils.success("成功");
        }
        return ResultUtils.error("失败");
    }

    /**
     * 编辑菜单
     */
    @PreAuthorize("hasAuthority('sys:menu:edit')")
    @PutMapping
    public ResultVo edit(@RequestBody SysMenu sysMenu){
        sysMenu.setUpdateTime(new Date());
        if(menuWebNetService.updateById(sysMenu)){
            return ResultUtils.success("成功");
        }
        return ResultUtils.error("失败");
    }



    /**
     * 根据用户ID获取其拥有的菜单列表 (用于生成动态路由)
     */
    @GetMapping("/getMenuList")
    public ResultVo getMenuList(Long userId){
        SysUser user = userWebService.getById(userId);
        List<SysMenu> menuList = menuWebNetService.getMenuByUserId(user.getUserId());

        // 过滤菜单数据，去掉按钮数据 (类型为 '2')
        List<SysMenu> collect = Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null &&  StringUtils.isNotEmpty(item.getType()) && !item.getType().equals("2")).collect(Collectors.toList());

        // 组装成前端需要的路由数据结构
        List<Router> rourer = MenuTree.makeRourer(collect, 0L);
        return ResultUtils.success("成功",rourer);
    }

    /**
     * 检查菜单路径在同一父目录下是否唯一
     */
    @GetMapping("/checkPath")
    public ResultVo checkPath(
            @RequestParam("path") String path,
            @RequestParam("parentId") Long parentId,
            @RequestParam(value = "menuId", required = false) Long menuId) {
        boolean exists = menuWebNetService.checkPathExists(path, parentId, menuId);
        return ResultUtils.success("查询成功", exists);
    }

    /**
     * 根据类型查询菜单列表
     */
    @GetMapping("/byType")
    public ResultVo getMenusByType(@RequestParam("type") String type) {
        List<SysMenu> list = menuWebNetService.findMenusByType(type);
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 获取指定菜单的面包屑导航路径
     */
    @GetMapping("/{menuId}/breadcrumbs")
    public ResultVo getBreadcrumbs(@PathVariable("menuId") Long menuId) {
        List<SysMenu> path = menuWebNetService.getParentPath(menuId);
        return ResultUtils.success("查询成功", path);
    }

    /**
     * 批量更新菜单排序
     */
    @PreAuthorize("hasAuthority('sys:menu:edit')")
    @PutMapping("/reorder")
    public ResultVo reorderMenus(@RequestBody List<SysMenu> menus) {
        if (menuWebNetService.updateOrderBatch(menus)) {
            return ResultUtils.success("更新排序成功");
        }
        return ResultUtils.error("更新排序失败");
    }

    /**

     * 获取指定菜单的所有子孙菜单
     */
    @GetMapping("/{menuId}/children/all")
    public ResultVo getAllChildren(@PathVariable("menuId") Long menuId) {
        List<SysMenu> children = menuWebNetService.getAllChildren(menuId);
        return ResultUtils.success("查询成功", children);
    }


    /**
     * 删除菜单
     */
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    @DeleteMapping("/{menuId}")
    public ResultVo delete(@PathVariable("menuId") Long menuId){
        // 检查是否存在下级菜单
        QueryWrapper<SysMenu> query = new QueryWrapper<>();
        query.lambda().eq(SysMenu::getParentId,menuId);
        List<SysMenu> list = menuWebNetService.list(query);

        if(list.size() > 0){
            return ResultUtils.error("该菜单存在下级，不能删除!");
        }

        if(menuWebNetService.removeById(menuId)){
            return ResultUtils.success("成功");
        }
        return ResultUtils.error("失败");
    }

    /**
     * 获取菜单列表 (树形结构)
     */
    @GetMapping("/list")
    public ResultVo getList(){
        // 查询所有菜单，并按排序字段升序
        QueryWrapper<SysMenu> query = new QueryWrapper<>();
        query.lambda().orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> list = menuWebNetService.list(query);

        // 组装树形结构数据 (从根节点 0L 开始)
        List<SysMenu> menuList = MenuTree.makeTree(list, 0L);
        return ResultUtils.success("成功",menuList);
    }

    /**
     * 获取上级菜单下拉树数据
     */
    @GetMapping("/getParent")
    public ResultVo getParent(){
        // 获取用于选择父级菜单的树结构
        List<SysMenu> list = menuWebNetService.getParent();
        return ResultUtils.success("成功",list);
    }


}