package com.itmk.netSystem.call.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

@Data
@TableName("make_order")
public class MakeOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer makeId;

    private Integer scheduleId;

    private Integer userId;

    private Integer visitUserId;

    private Integer doctorId;

    private String times;

    private String timesArea;

    private String week;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    private BigDecimal price;

    private String address;

    private String status;

    private String hasCall;

    private String hasVisit;

    @TableField(exist = false)
    private String visitname;
    @TableField(exist = false)
    private String deptName;
    @TableField(exist = false)
    private String nickName;

    public boolean isMorningAppointment() {
        return "0".equals(this.timesArea);
    }

    public boolean isCancelled() {
        return "2".equals(this.status);
    }

    public boolean isPendingVisit() {
        return "1".equals(this.status) && "0".equals(this.hasVisit);
    }



    public String getFormattedAppointmentTime() {
        return this.week + " " + this.times + (("0".equals(this.timesArea)) ? "(上午)" : "(下午)");
    }

}