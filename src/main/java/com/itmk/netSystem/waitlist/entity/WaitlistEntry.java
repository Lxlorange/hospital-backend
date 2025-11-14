package com.itmk.netSystem.waitlist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("waitlist_entry")
public class WaitlistEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer scheduleId;
    private Integer doctorId;
    private Integer userId;
    private Integer visitUserId;

    /**
     * 候补状态：pending=待候补, allocated=已分配, canceled=已取消
     */
    private String status;

    /** 候补优先级（数字越小优先级越高），默认按创建时间排序 */
    private Integer priority;

    private Date createTime;
    private Date updateTime;
}