package com.itmk.netSystem.roleWeb.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.netSystem.roleWeb.entity.RoleNum;
import com.itmk.netSystem.roleWeb.entity.SysRole;
import com.itmk.netSystem.roleWeb.entity.roleWeb;
import com.itmk.netSystem.roleWeb.service.roleWebService;
import com.itmk.netSystem.roleWebNetMenu.entity.MenuNum;
import com.itmk.netSystem.roleWebNetMenu.service.roleWebNetMenuService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

// 系统角色管理控制器
@RequestMapping("/api/role")
@RestController
public class roleWebController {
    @Autowired
    private roleWebService roleWebService; // 系统角色服务
    @Autowired
    private roleWebNetMenuService roleWebNetMenuService; // 角色菜单关联服务

    /**
     * 新增角色
     */
    @PreAuthorize("hasAuthority('sys:role:add')")
    @PostMapping
    public ResultVo add(@RequestBody SysRole sysRole){
        sysRole.setCreateTime(new Date());
        if(roleWebService.save(sysRole)){
            return ResultUtils.success("成功");
        }
        return ResultUtils.error("失败");
    }

    /**
     * 根据ID获取角色详情
     */
    @GetMapping("/{roleId}")
    public ResultVo getRoleById(@PathVariable("roleId") Long roleId) {
        SysRole role = roleWebService.getById(roleId);
        if (role == null) {
            return ResultUtils.error("未找到角色");
        }
        return ResultUtils.success("查询成功", role);
    }

    /**
     * 检查角色名称是否唯一
     */
    @GetMapping("/checkRoleName")
    public ResultVo checkRoleName(@RequestParam("roleName") String roleName, @RequestParam(value = "roleId", required = false) Long roleId) {
        boolean exists = roleWebService.checkRoleName(roleName, roleId);
        return ResultUtils.success("查询成功", exists);
    }

    /**
     * 批量删除角色
     */
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @DeleteMapping("/batch")
    public ResultVo batchDelete(@RequestBody List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return ResultUtils.error("请提供要删除的角色ID");
        }
        roleWebService.batchDelete(roleIds);
        return ResultUtils.success("批量删除成功");
    }



    /**
     * 更新角色状态
     * 注意: 此功能假设SysRole实体类中有status字段, 或通过其他字段模拟
     */
    @PreAuthorize("hasAuthority('sys:role:edit')")
    @PutMapping("/status")
    public ResultVo updateStatus(@RequestBody SysRole sysRole) {
        if (roleWebService.updateStatus(sysRole.getRoleId(), "demo_status_value")) {
            // 实际场景中，状态值应从sysRole对象中获取, 如 sysRole.getStatus()
            return ResultUtils.success("状态更新成功");
        }
        return ResultUtils.error("状态更新失败");
    }

    /**
     * 编辑角色
     */
    @PreAuthorize("hasAuthority('sys:role:edit')")
    @PutMapping
    public ResultVo edit(@RequestBody SysRole sysRole){
        sysRole.setUpdateTime(new Date());
        if(roleWebService.updateById(sysRole)){
            return ResultUtils.success("成功");
        }
        return ResultUtils.error("失败");
    }

    /**
     * 删除角色
     */
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @DeleteMapping("/{roleId}")
    public ResultVo delete(@PathVariable("roleId") Long roleId){
        // 调用服务删除角色及关联数据
        roleWebService.delete(roleId);
        return ResultUtils.success("成功");
    }

    /**
     * 角色列表查询 (分页)
     */
    @GetMapping("/getList")
    public ResultVo getList(RoleNum parm){
        // 构造分页对象
        IPage<SysRole> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        // 构造查询条件
        QueryWrapper<SysRole> query = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(parm.getRoleName())){
            // 角色名称模糊查询
            query.lambda().like(SysRole::getRoleName,parm.getRoleName());
        }
        query.lambda().orderByDesc(SysRole::getCreateTime);
        IPage<SysRole> list = roleWebService.page(page, query);
        return ResultUtils.success("成功",list);
    }

    /**
     * 获取角色下拉列表数据
     */
    @GetMapping("/selectList")
    public ResultVo selectList(){
        List<SysRole> list = roleWebService.list();
        // 封装返回的下拉列表数据
        List<roleWeb> roleWebs = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(item ->{
                    roleWeb vo = new roleWeb();
                    vo.setCheck(false);
                    vo.setLabel(item.getRoleName());
                    vo.setValue(item.getRoleId());
                    roleWebs.add(vo);
                });
        return  ResultUtils.success("成功", roleWebs);
    }

    /**
     * 保存角色和菜单的关联关系
     */
    @PreAuthorize("hasAuthority('sys:role:assign')")
    @PostMapping("/saveRoleMenu")
    public ResultVo saveRoleMenu(@RequestBody MenuNum parm){
        // 保存角色分配的菜单
        roleWebNetMenuService.saveRoleMenu(parm);
        return  ResultUtils.success("成功");
    }
}
