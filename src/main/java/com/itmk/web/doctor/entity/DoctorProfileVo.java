package com.itmk.web.doctor.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 医生个人主页信息VO
 */
@Data
public class DoctorProfileVo {
    /** 医生ID */
    private Long userId;
    
    /** 用户名 */
    private String username;
    
    /** 手机号 */
    private String phone;
    
    /** 邮箱 */
    private String email;
    
    /** 性别 */
    private String sex;
    
    /** 昵称/姓名 */
    private String nickName;
    
    /** 科室ID */
    private Integer deptId;
    
    /** 科室名称 */
    private String deptName;
    
    /** 学历 */
    private String education;
    
    /** 职称 */
    private String jobTitle;
    
    /** 头像 */
    private String image;
    
    /** 简介 */
    private String introduction;
    
    /** 出诊地址 */
    private String visitAddress;
    
    /** 擅长治疗的病症 */
    private String goodAt;
    
    /** 挂号费 */
    private BigDecimal price;
    
    /** 是否推荐到首页 */
    private String toHome;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 更新时间 */
    private Date updateTime;
    
    /** 医生状态 */
    private boolean enabled;
    
    /** 出诊信息（最近一周） */
    @TableField(exist = false)
    private Object scheduleInfo;
    
    /** 预约数量统计 */
    @TableField(exist = false)
    private Object appointmentStats;
}