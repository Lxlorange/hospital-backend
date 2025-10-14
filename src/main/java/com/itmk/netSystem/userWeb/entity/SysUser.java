package com.itmk.netSystem.userWeb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

// 系统用户实体类，实现UserDetails用于Spring Security认证
@Data
@TableName("sys_user")
public class SysUser implements UserDetails {
    @TableId(type = IdType.AUTO)
    private Long userId; // 用户ID
    private String username; // 用户名
    private String password; // 密码
    private String phone; // 电话
    private String email; // 邮箱
    private String sex; // 性别
    private String isAdmin; // 是否为超级管理员 (1:是, 0:否)

    // 不属于用户表，用于接收角色ID，MyBatis Plus忽略此字段
    @TableField(exist = false)
    private String roleId;

    // 帐户是否过期 (true: 未过期)
    private boolean isAccountNonExpired = true;
    // 帐户是否被锁定 (true: 未锁定)
    private boolean isAccountNonLocked = true;
    // 密码是否过期 (true: 未过期)
    private boolean isCredentialsNonExpired = true;
    // 帐户是否可用 (true: 可用)
    private boolean isEnabled = true;

    private String nickName; // 昵称
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;

    // 用户权限字段的集合，MyBatis Plus忽略此字段
    @TableField(exist = false)
    Collection<? extends GrantedAuthority> authorities;

    // 科室ID
    private Integer deptId;
    // 学历
    private String education;
    // 职称
    private String jobTitle;
    // 头像
    private String image;
    // 简介
    private String introduction;
    // 出诊地址
    private String visitAddress;

    @TableField(exist = false)
    private String deptName; // 科室名称

    // 是否推荐到首页 0：未推荐  1：推荐
    private String toHome;
    // 擅长治疗的病症
    private String goodAt;
    // 挂号费
    private BigDecimal price;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}