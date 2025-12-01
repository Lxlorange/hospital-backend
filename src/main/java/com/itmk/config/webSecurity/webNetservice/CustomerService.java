package com.itmk.config.webSecurity.webNetservice;

import com.itmk.config.webSecurity.exception.CustomerException;
import com.itmk.netSystem.menuWebNet.entity.SysMenu;
import com.itmk.netSystem.menuWebNet.service.menuWebNetService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 自定义用户详情服务，用于Spring Security认证
@Component("customerUserDetailService")
public class CustomerService implements UserDetailsService {
    @Autowired
    private userWebService userWebService; // 系统用户服务
    @Autowired
    private menuWebNetService menuWebNetService; // 系统菜单服务

    /**
     * 根据用户邮箱加载用户详情 (假设 service 和 mapper 支持)
     * @param email 用户邮箱
     * @return UserDetails 用户详情对象
     * @throws UsernameNotFoundException 如果用户不存在
     */
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        // 假设 userWebService 中有 loadUserByEmail 方法
        // SysUser user = userWebService.loadUserByEmail(email);
        // if(user == null) throw new CustomerException("邮箱不存在");
        // return this.buildUserDetails(user);
        // 此处为示例，返回一个模拟用户
        return loadUserByUsername("admin");
    }


    private void checkUserStatus(SysUser user) {
        // 假设SysUser实体有 isLocked, isEnabled 等字段
        // if (user.isLocked()) throw new LockedException("账户已被锁定");
        // if (!user.isEnabled()) throw new DisabledException("账户已被禁用");
    }

    /**
     * 将权限字符串列表转换为Spring Security的GrantedAuthority列表
     * @param permissions 权限字符串列表
     * @return List<GrantedAuthority>
     */
    private List<GrantedAuthority> mapToGrantedAuthorities(List<String> permissions) {
        String[] permissionArray = permissions.toArray(new String[0]);
        return AuthorityUtils.createAuthorityList(permissionArray);
    }

    /**
     * 从数据库菜单列表中提取权限码
     * @param menuList 菜单列表
     * @return 权限码字符串列表
     */
    private List<String> extractPermissionCodes(List<SysMenu> menuList) {
        return Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null && StringUtils.isNotEmpty(item.getCode()))
                .map(SysMenu::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 封装构建UserDetails的核心逻辑
     * @param user 系统用户实体
     * @return UserDetails
     */
    private UserDetails buildUserDetails(SysUser user) {
        checkUserStatus(user);
        List<SysMenu> menuList = menuWebNetService.getMenuByUserId(user.getUserId());
        List<String> permissions = extractPermissionCodes(menuList);
        List<GrantedAuthority> authorities = mapToGrantedAuthorities(permissions);
        user.setAuthorities(authorities);
        return user;
    }

    /**
     * 根据用户名加载用户详情，包括权限信息
     * @param username 用户名
     * @return UserDetails 用户详情对象
     * @throws UsernameNotFoundException 如果用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户
        SysUser user = userWebService.loadUser(username);
        if(user == null){
            throw new CustomerException("用户名错误");
        }

        List<SysMenu> menuList = menuWebNetService.getMenuByUserId(user.getUserId());

        // 提取菜单表的 'code' 字段作为权限标识
        List<String> collect = Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null && StringUtils.isNotEmpty(item.getCode()))
                .map(SysMenu::getCode)
                .collect(Collectors.toList());

        // 将权限标识转换为GrantedAuthority集合
        String[] strings = collect.toArray(new String[collect.size()]);
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(strings);

        // 设置用户权限
        user.setAuthorities(authorityList);
        return user;
    }
}