package com.itmk.netSystem.leaveRequest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 医生请假申请表
 */
@Data
@TableName("leave_request")
public class LeaveRequest {
    /** 申请ID */
    @TableId(type = IdType.AUTO)
    private Long requestId;

    /** 医生ID（字符串，示例：D001） */
    private String doctorId;

    /** 医生昵称 */
    private String nickName;

    /** 开始日期（YYYY-MM-DD） */
    private String startDate;

    /** 结束日期（YYYY-MM-DD） */
    private String endDate;

    /** 开始时段（0=上午，1=下午） */
    private String startTime;

    /** 结束时段（0=上午，1=下午） */
    private String endTime;

    /** 请假原因 */
    private String reason;

    /** 状态：0=待审核，1=通过，2=拒绝 */
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