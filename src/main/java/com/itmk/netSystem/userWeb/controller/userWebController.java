package com.itmk.netSystem.userWeb.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.itmk.tool.Utils;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.teamDepartment.entity.teamDepartment;
import com.itmk.netSystem.menuWebNet.entity.AssignTreeNum;
import com.itmk.netSystem.menuWebNet.entity.AssignTree;
import com.itmk.netSystem.menuWebNet.entity.SysMenu;
import com.itmk.netSystem.menuWebNet.service.menuWebNetService;
import com.itmk.netSystem.userWeb.entity.*;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.netSystem.netRole.entity.SysUserRole;
import com.itmk.netSystem.netRole.service.NetRoleService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.*;
import java.util.stream.Collectors;

// 系统用户管理控制器
@RequestMapping("/api/sysUser")
@RestController
public class userWebController {
    @Autowired
    private userWebService userWebService;
    @Autowired
    private NetRoleService netRoleService;
    @Autowired
    private DefaultKaptcha defaultKaptcha; // 验证码生成器
    @Autowired
    private Utils jwtUtils; // JWT工具
    @Autowired
    private menuWebNetService menuWebNetService;
    @Autowired
    private AuthenticationManager authenticationManager; // Spring Security认证管理器
    @Autowired
    private PasswordEncoder passwordEncoder; // 密码编码器

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public ResultVo login(HttpServletRequest request, @RequestBody LoginNum parm) {
        // 验证码验证逻辑

        // 认证用户名和密码
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(parm.getUsername(),parm.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        // 将认证信息设置到SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        // 获取用户信息
        SysUser user = (SysUser)authenticate.getPrincipal();

        // 返回用户信息和token
        Login vo = new Login();
        vo.setUserId(user.getUserId());
        vo.setNickName(user.getNickName());

        // 判断是否为超级管理员
        if(StringUtils.isNotEmpty(user.getIsAdmin()) && user.getIsAdmin().equals("1")){
            vo.setType("1"); // 超级管理员
        }else{
            vo.setType("0"); // 普通用户
        }

        // 生成JWT token
        Map<String, String> map = new HashMap<>();
        map.put("userId", Long.toString(user.getUserId()));
        map.put("username",user.getUsername());
        String token = jwtUtils.generateToken(map);
        vo.setToken(token);

        return ResultUtils.success("成功", vo);
    }

    /**
     * 获取图片验证码
     */
    @PostMapping("/getImage")
    public ResultVo imageCode(jakarta.servlet.http.HttpServletRequest request) {
        // 获取session
        jakarta.servlet.http.HttpSession session = request.getSession();
        // 生成验证码文本
        String text = defaultKaptcha.createText();
        // 存放到session
        session.setAttribute("code", text);
        // 生成图片并转为base64
        BufferedImage bufferedImage = defaultKaptcha.createImage(text);
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
            String base64 = Base64.encodeBase64String(outputStream.toByteArray());
            String captchaBase64 = "data:image/jpeg;base64," + base64.replaceAll("\r\n", "");
            ResultVo result = new ResultVo("生成成功", 200, captchaBase64);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 用户退出登录
     */
    @PostMapping("/loginOut")
    public ResultVo loginOut(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            // 执行Spring Security的退出操作
            new SecurityContextLogoutHandler().logout(request,response,authentication);
        }
        return ResultUtils.success("成功!");
    }

    /**
     * 获取当前登录用户的信息及权限列表
     */
    @GetMapping("/getInfo")
    public ResultVo getInfo(Long userId) {
        // 根据id查询用户信息
        SysUser user = userWebService.getById(userId);
        List<SysMenu> menuList = null;

        menuList = menuWebNetService.getMenuByUserId(user.getUserId());

        // 提取菜单表的code字段作为权限标识
        List<String> collect = Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null && StringUtils.isNotEmpty(item.getCode()))
                .map(SysMenu::getCode)
                .collect(Collectors.toList());

        // 设置返回值
        UserInformation userInformation = new UserInformation();
        userInformation.setName(user.getNickName());
        userInformation.setUserId(user.getUserId());
        userInformation.setPermissons(collect.toArray()); // 权限列表
        return ResultUtils.success("成功", userInformation);
    }

    /**
     * 新增用户
     */
    @PreAuthorize("hasAuthority('sys:user:add')")
    @PostMapping
    public ResultVo add(@RequestBody SysUser sysUser) {
        sysUser.setCreateTime(new Date());
        sysUser.setIsAdmin("0"); // 设置为普通用户
        // 密码加密
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        userWebService.saveUser(sysUser);
        return ResultUtils.success("成功新增");
    }

    /**
     * 编辑用户信息
     */
    @PreAuthorize("hasAuthority('sys:user:edit')")
    @PutMapping
    public ResultVo edit(@RequestBody SysUser sysUser) {
        sysUser.setUpdateTime(new Date());
        userWebService.editUser(sysUser);
        return ResultUtils.success("成功编辑");
    }

    /**
     * 删除用户
     */
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @DeleteMapping("/{userId}")
    public ResultVo delete(@PathVariable("userId") Long userId) {
        userWebService.deleteUser(userId);
        return ResultUtils.success("成功删除");
    }


    /**
     * 检查用户名是否唯一
     */
    @GetMapping("/checkUsername")
    public ResultVo checkUsername(@RequestParam("username") String username) {
        boolean exists = userWebService.checkUsername(username);
        if (exists) {
            return ResultUtils.error("用户名已存在", true);
        }
        return ResultUtils.success("用户名可用", false);
    }

    /**
     * 获取当前登录用户的个人资料
     */
    @GetMapping("/profile")
    public ResultVo getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser userDetails = (SysUser) authentication.getPrincipal();
        SysUser userProfile = userWebService.getUserProfile(userDetails.getUserId());
        return ResultUtils.success("查询成功", userProfile);
    }

    /**
     * 用户更新自己的个人资料
     */
    @PutMapping("/profile")
    public ResultVo updateProfile(@RequestBody SysUser sysUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser userDetails = (SysUser) authentication.getPrincipal();
        // 确保用户只能修改自己的信息
        sysUser.setUserId(userDetails.getUserId());
        if (userWebService.updateUserProfile(sysUser)) {
            return ResultUtils.success("更新成功");
        }
        return ResultUtils.error("更新失败");
    }

    /**
     * 批量删除用户
     */
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @DeleteMapping("/batch")
    public ResultVo batchDelete(@RequestBody List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return ResultUtils.error("请提供要删除的用户ID");
        }
        userWebService.batchDeleteUsers(userIds);
        return ResultUtils.success("批量删除成功");
    }

    /**
     * 根据邮箱查找用户
     */
    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/findByEmail")
    public ResultVo findByEmail(@RequestParam("email") String email) {
        SysUser user = userWebService.findByEmail(email);
        if (user == null) {
            return ResultUtils.error("未找到该邮箱对应的用户");
        }
        return ResultUtils.success("查询成功", user);
    }

    /**
     * 修改用户密码
     */
    @PostMapping("/updatePassword")
    public ResultVo updatePassword(@RequestBody resetPasswordParm parm) {
        SysUser user = userWebService.getById(parm.getUserId());

        // 验证原密码是否正确
        if (!passwordEncoder.matches(parm.getOldPassword(),user.getPassword())) {
            return ResultUtils.error("原密码错误!");
        }

        // 更新条件，设置新密码
        UpdateWrapper<SysUser> query = new UpdateWrapper<>();
        query.lambda().set(SysUser::getPassword, passwordEncoder.encode(parm.getPassword()))
                .eq(SysUser::getUserId, parm.getUserId());

        if (userWebService.update(query)) {
            return ResultUtils.success("修改成功");
        }
        return ResultUtils.error("修改失败");
    }

    /**
     * 用户列表查询 (分页)
     */
    @GetMapping("/list")
    public ResultVo list(userWebPage parm) {
        // 构造分页对象
        IPage<SysUser> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        // 构造查询条件 (多表联查用户和科室)
        MPJLambdaWrapper<SysUser> query = new MPJLambdaWrapper<>();
        query.selectAll(SysUser.class)
                .select(Department::getDeptName)
                .leftJoin(Department.class,Department::getDeptId,SysUser::getDeptId)
                .eq(SysUser::getIsAdmin,"0"); // 只查询普通用户

        if (StringUtils.isNotEmpty(parm.getNickName())) {
            query.like(SysUser::getNickName, parm.getNickName());
        }
        if (StringUtils.isNotEmpty(parm.getPhone())) {
            query.like(SysUser::getPhone, parm.getPhone());
        }
        query.orderByDesc(SysUser::getCreateTime);

        // 查询列表
        IPage<SysUser> list = userWebService.page(page, query);
        return ResultUtils.success("成功查询", list);
    }


    /**
     * 根据用户id查询用户的角色列表
     */
    @GetMapping("/getRoleList")
    public ResultVo getRoleList(Long userId) {
        QueryWrapper<SysUserRole> query = new QueryWrapper<>();
        query.lambda().eq(SysUserRole::getUserId, userId);
        List<SysUserRole> list = netRoleService.list(query);

        // 提取角色ID
        List<Long> roleList = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(item -> {
                    roleList.add(item.getRoleId());
                });
        return ResultUtils.success("成功查询", roleList);
    }



    /**
     * 启用/停用用户
     */
    @PreAuthorize("hasAuthority('sys:user:enabled')")
    @PostMapping("/enabledUser")
    public ResultVo enabledUser(@RequestBody SysUser sysUser) {
        userWebService.updateById(sysUser);
        return ResultUtils.success("成功操作");
    }

    /**
     * 查询医生下拉列表数据 (根据科室ID)
     */
    @GetMapping("/getSelectUser")
    public ResultVo getSelectUser(Long deptId){
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getDeptId,deptId).orderByDesc(SysUser::getUsername);
        List<SysUser> list = userWebService.list(query);
        List<teamDepartment> deptList = new ArrayList<>();

        // 封装为下拉框需要的格式
        if(list.size() > 0){
            for (int i=0;i<list.size();i++){
                teamDepartment dept = new teamDepartment();
                dept.setLabel(list.get(i).getNickName());
                dept.setValue(Integer.parseInt(list.get(i).getUserId().toString()));
                deptList.add(dept);
            }

        }
        return ResultUtils.success("成功查询",deptList);
    }

    /**
     * 重置用户密码为默认值
     */
    @PreAuthorize("hasAuthority('sys:user:reset')")
    @PostMapping("/resetPassword")
    public ResultVo resetPassword(@RequestBody SysUser sysUser) {
        // 更新条件，设置默认密码并加密
        UpdateWrapper<SysUser> query = new UpdateWrapper<>();
        query.lambda().eq(SysUser::getUserId, sysUser.getUserId())
                .set(SysUser::getPassword, passwordEncoder.encode("666666"));
        if (userWebService.update(query)) {
            return ResultUtils.success("重置成功");
        }
        return ResultUtils.error("重置失败");
    }

    /**
     * 查询用户权限菜单树结构
     */
    @GetMapping("/getAssingTree")
    public ResultVo getAssingTree(AssignTreeNum parm) {
        AssignTree assignTree = userWebService.getAssignTree(parm);
        return ResultUtils.success("成功查询", assignTree);
    }





}