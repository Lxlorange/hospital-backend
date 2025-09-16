package com.itmk.web.sys_user.entity;

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

 
@Data
@TableName("sys_user")
public class SysUser implements UserDetails {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String sex;
    private String isAdmin;
    //不属于用户表，需要排除
    @TableField(exist = false)
    private String roleId;
    //帐户是否过期(1 未过期，0已过期)
    private boolean isAccountNonExpired = true;
    //帐户是否被锁定(1 未锁定，0已锁定)
    private boolean isAccountNonLocked = true;
    //密码是否过期(1 未过期，0已过期)
    private boolean isCredentialsNonExpired = true;
    //帐户是否可用(1 可用，0 停用)
    private boolean isEnabled = true;
    private String nickName;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //用户权限字段的集合
    @TableField(exist = false)
    Collection<? extends GrantedAuthority> authorities;
    //科室id
    private Integer deptId;
    //学历
    private String education;
    //职称
    private String jobTitle;
    //头像
    private String image;
    //简介
    private String introduction;
    //出诊地址
    private String visitAddress;
    @TableField(exist = false)
    private String deptName;
    /** 是否推荐到首页 0：未推荐  1：推荐 */
    private String toHome;
    //擅长治疗的病症
    private String goodAt;
    //挂号费
    private BigDecimal price;
}