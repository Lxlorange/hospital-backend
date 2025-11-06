package com.itmk.netSystem.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 身份认证申请表
 */
@Data
@TableName("identity_auth_request")
public class IdentityAuthRequest {
    /** 申请ID */
    @TableId(type = IdType.AUTO)
    private Long requestId;

    /** 提交用户ID（小程序用户） */
    private Integer userId;

    /** 提交用户名（展示用） */
    private String username;

    /** 用户类型，如：学生、教师等 */
    private String type;

    /** 证件号码/学号等 */
    private String code;

    /** 证件照（正面） */
    private String frontPhoto;

    /** 证件照（反面） */
    private String backPhoto;

    /** 状态：0=待审核，1=通过，2=拒绝 */
    private String status;

    /** 审核意见 */
    private String reviewComment;

    /** 审核人ID（后台用户） */
    private Long reviewerId;

    /** 审核时间 */
    private Date reviewTime;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}