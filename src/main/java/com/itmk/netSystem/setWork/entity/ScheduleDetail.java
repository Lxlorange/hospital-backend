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
    private Integer amount;
    private Integer lastAmount;
    private String type;
    @TableField(exist = false)
    private Integer deptId;
    @TableField(exist = false)
    private String deptName;
    @TableField(exist = false)
    private BigDecimal price;


    /**
     * 业务逻辑方法：判断当前排班是否已挂满
     * @return 如果剩余号源小于或等于0，则返回 true；否则返回 false。
     */
    public boolean isFullyBooked() {
        return this.lastAmount != null && this.lastAmount <= 0;
    }

    /**
     * 业务逻辑方法：减少一个剩余号源
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

    public ScheduleDetail lastAmount(Integer lastAmount) {
        this.lastAmount = lastAmount;
        return this;
    }
}