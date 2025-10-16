package com.itmk.netSystem.schedule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 排班实例主表实体
 */
@Data
@TableName("schedule_instance")
public class ScheduleInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 实例ID
     */
    @TableId(value = "instance_id", type = IdType.AUTO)
    private Long instanceId;

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 具体出诊日期 (格式: "YYYY-MM-DD")
     */
    private LocalDate scheduleDate;

    /**
     * 时间段 (1=上午, 2=下午)
     */
    private Integer timeSlot;

    /**
     * 状态 (1=正常, 2=停诊, 3=约满)
     */
    private Integer status;

    @TableField(exist = false)
    private String doctorName;

    // 用于接收连表查询出的科室名称
    @TableField(exist = false)
    private String departmentName;

    // 用于封装当前实例下的所有号源详情
    @TableField(exist = false)
    private List<InstanceSlot> slots;

}