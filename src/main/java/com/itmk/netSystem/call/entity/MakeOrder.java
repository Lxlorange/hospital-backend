package com.itmk.netSystem.call.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
    @TableField(exist = false)
    private GeetestParam geetest;

    // 内部类，用于匹配前端传来的 geetest 对象
    // (注意: 必须是 static 才能被 Jackson 正常反序列化)
    public static class GeetestParam implements Serializable {
        private String lot_number;
        private String pass_token;
        private String gen_time;
        private String captcha_output;

        // (为 GeetestParam 添加 Getters 和 Setters)
        public String getLot_number() { return lot_number; }
        public void setLot_number(String lot_number) { this.lot_number = lot_number; }
        public String getPass_token() { return pass_token; }
        public void setPass_token(String pass_token) { this.pass_token = pass_token; }
        public String getGen_time() { return gen_time; }
        public void setGen_time(String gen_time) { this.gen_time = gen_time; }
        public String getCaptcha_output() { return captcha_output; }
        public void setCaptcha_output(String captcha_output) { this.captcha_output = captcha_output; }
    }

    // (为 GeetestParam 字段添加 Getter 和 Setter)
    public GeetestParam getGeetest() { return geetest; }
    public void setGeetest(GeetestParam geetest) { this.geetest = geetest; }

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