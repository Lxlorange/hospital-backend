package com.itmk.netSystem.schedule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 实例号源详情实体
 */
@Data
@TableName("instance_slot")
public class InstanceSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 实例号源ID
     */
    @TableId(value = "instance_slot_id", type = IdType.AUTO)
    private Long instanceSlotId;

    /**
     * 关联的实例ID
     */
    private Long instanceId;

    /**
     * 号源类型
     */
    private String slotType;

    /**
     * 总号量
     */
    private Integer totalAmount;

    /**
     * 可用号量 (每次挂号后需减1)
     */
    private Integer availableAmount;

    /**
     * 挂号费
     */
    private BigDecimal price;

}