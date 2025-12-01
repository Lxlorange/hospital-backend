package com.itmk.netSystem.schedule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 医生排班模板主表实体
 */
@Data
@TableName("schedule_template")
public class ScheduleTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "template_id", type = IdType.AUTO)
    private Long templateId;

    private Long doctorId;
    private Integer dayOfWeek;
    private Integer timeSlot;

    // 用于封装当前模板下的所有号源设置
    @TableField(exist = false)
    private List<TemplateSlot> slots;
}