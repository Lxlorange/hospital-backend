package com.itmk.netSystem.schedule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 模板号源详情实体
 */
@Data
@TableName("template_slot")
public class TemplateSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 号源ID
     */
    @TableId(value = "slot_id", type = IdType.AUTO)
    private Long slotId;

    /**
     * 关联的模板ID
     */
    private Long templateId;

    /**
     * 号源类型 (如 "普通号", "专家号")
     */
    private String slotType;

    /**
     * 总号量
     */
    private Integer totalAmount;

    /**
     * 挂号费
     */
    private BigDecimal price;

}