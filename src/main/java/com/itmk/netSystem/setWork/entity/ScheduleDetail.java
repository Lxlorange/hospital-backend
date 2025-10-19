package com.itmk.netSystem.setWork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName("schedule_detail")
public class ScheduleDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer scheduleId;

    private Integer doctorId;

    private String doctorName;

    private String times;

    private String week;

    private Integer witchWeek;

    // --- 主要修改点 ---
    /**
     * 号别名称 (数据库字段)
     * 用于存储 "普通号", "专家号" 等, 这是计算价格的依据。
     */
    private String levelName;

    /**
     * 可以用 0 表示上午，1 表示下午
     */
    private Integer timeSlot;

    /**
     * 挂号费
     */
    private Integer price;

    private Integer amount;

    private Integer lastAmount;

    private String type;


    // --- 非数据库字段，用于业务逻辑和数据传输 ---

    @TableField(exist = false)
    private Integer deptId;

    @TableField(exist = false)
    private String deptName;



    // --- 业务逻辑方法 ---

    /**
     * 判断当前排班是否已挂满
     * @return 如果剩余号源小于或等于0，则返回 true；否则返回 false。
     */
    public boolean isFullyBooked() {
        return this.lastAmount != null && this.lastAmount <= 0;
    }

    /**
     * 减少一个剩余号源
     * 如果号源充足，则减少一个并返回true；否则返回false。
     */
    public boolean decreaseLastAmount() {
        if (this.lastAmount != null && this.lastAmount > 0) {
            this.lastAmount--;
            return true;
        }
        return false;
    }

    // --- Fluent Setters for chaining ---

    public ScheduleDetail scheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
        return this;
    }

    public ScheduleDetail doctorId(Integer doctorId) {
        this.doctorId = doctorId;
        return this;
    }

    public ScheduleDetail doctorName(String doctorName) {
        this.doctorName = doctorName;
        return this;
    }

    public ScheduleDetail levelName(String levelName) {
        this.levelName = levelName;
        return this;
    }


    public ScheduleDetail lastAmount(Integer lastAmount) {
        this.lastAmount = lastAmount;
        return this;
    }
}