package com.itmk.netSystem.doctor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 医生信息更新申请表
 */
@Data
@TableName("doctor_update_request")
public class DoctorUpdateRequest {
    /** 申请ID */
    @TableId(type = IdType.AUTO)
    private Long requestId;
    
    /** 医生ID */
    private Long doctorId;
    
    /** 医生用户名 */
    private String username;
    
    /** 医生姓名 */
    private String nickName;
    
    /** 简介 */
    private String introduction;
    
    /** 出诊地址 */
    private String visitAddress;
    
    /** 擅长治疗的病症 */
    private String goodAt;
    
    /** 挂号费 */
    private BigDecimal price;
    
    /** 申请状态：0-待审核，1-已通过，2-已拒绝 */
    private String status;
    
    /** 审核意见 */
    private String reviewComment;
    
    /** 审核人ID */
    private Long reviewerId;
    
    /** 审核时间 */
    private Date reviewTime;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 更新时间 */
    private Date updateTime;
}