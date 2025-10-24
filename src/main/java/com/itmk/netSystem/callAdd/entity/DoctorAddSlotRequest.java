package com.itmk.netSystem.callAdd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("doctor_add_slot_request")
public class DoctorAddSlotRequest implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long requestId;

    private Integer scheduleId;
    private Integer doctorId;
    private Integer userId;
    private Integer visitUserId;

    /** 0=上午, 1=下午 */
    private String timesArea;
    /** 排班日期(yyyy-MM-dd) */
    private String times;
    private String week;

    private BigDecimal price;
    private String address;

    /** 申请原因 */
    private String reason;

    /** 状态：0-待审核，1-已通过，2-已拒绝 */
    private String status;

    /** 审核备注 */
    private String reviewComment;
    private Long reviewerId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date reviewTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;
}