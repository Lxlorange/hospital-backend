package com.itmk.netSystem.leaveRequest.entity;

import lombok.Data;

import java.util.Date;

/**
 * 管理员审核列表返回项
 */
@Data
public class LeaveRequestListItem {
    private Long requestId;
    private String doctorId;
    private String nickName;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String reason;
    private String status;
    private String reviewComment;
    private Long reviewerId;
    private Date reviewTime;
    private Date createTime;
}