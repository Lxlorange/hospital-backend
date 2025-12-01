package com.itmk.netSystem.setWork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("schedule_detail")
public class ScheduleDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "schedule_id", type = IdType.AUTO)
    private Integer scheduleId;

    private Integer doctorId;
    private String doctorName;

    // 建议的修改：将 times 字段类型改为 LocalDate 以匹配数据库的 DATE 类型
    private LocalDate times;

    private String week;
    private Integer witchWeek;




    /**
     * 时段 (0=上午, 1=下午) (新增)
     */
    private Integer timeSlot;

    /**
     * 号别类型 (已存在)
     */
    private String levelName;

    /**
     * 挂号价格 (修改：现在是数据库字段)
     * 注意：数据库中 price 字段类型应为 DECIMAL(10,2)，而不是 INT
     */
    private BigDecimal price;


    private Integer amount;
    private Integer lastAmount;
    private String type;


    // --- 非数据库字段，用于业务逻辑和数据传输 ---

    @TableField(exist = false)
    private Integer deptId;

    @TableField(exist = false)
    private String deptName;



    public boolean isFullyBooked() {
        return this.lastAmount != null && this.lastAmount <= 0;
    }
    public boolean decreaseLastAmount() {
        if (this.lastAmount != null && this.lastAmount > 0) {
            this.lastAmount--;
            return true;
        }
        return false;
    }

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
    public ScheduleDetail price(BigDecimal price) {
        this.price = price;
        return this;
    }
    public ScheduleDetail lastAmount(Integer lastAmount) {
        this.lastAmount = lastAmount;
        return this;
    }
    public ScheduleDetail timeSlot(Integer timeSlot) {
        this.timeSlot = timeSlot;
        return this;
    }
    public ScheduleDetail type(String type) {
        this.type = type;
        return this;
    }
}