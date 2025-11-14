package com.itmk.netSystem.see.entity;
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
@TableName("make_order_visit")
public class MakeOrderVisit implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer visitId;
    private Integer makeId;
    private Integer userId;
    private Integer visitUserId;
    private Integer doctorId;
    private String times;
    private String timesArea;
    private String week;
    private String hasVisit;
    private String hasLive;
    private String advice;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date visitTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    @TableField(exist = false)
    private String visitname;
    @TableField(exist = false)
    private String deptName;
    @TableField(exist = false)
    private String nickName;
    @TableField(exist = false)
    private boolean collapsed = true;
    @TableField(exist = false)
    private String address;
    @TableField(exist = false)
    private BigDecimal price;

    /**
     * 检查本次就诊是否已经完成。
     * @return 如果'hasVisit'字段为"1"则返回true，否则返回false。
     */
    public boolean hasVisited() {
        return "1".equals(this.hasVisit);
    }

    /**
     * 检查病人在此次就诊后是否需要住院。
     * @return 如果'hasLive'字段为"1"则返回true，否则返回false。
     */
    public boolean requiresHospitalization() {
        return "1".equals(this.hasLive);
    }

    /**
     * 切换UI显示的折叠状态。
     * 这个方法对于前端控制详情的展开或折叠非常有用。
     */
    public void toggleCollapsed() {
        this.collapsed = !this.collapsed;
    }

    /**
     * 生成一个用于描述预约安排的摘要字符串。
     * @return 一个格式化后的、代表预约时间的字符串。
     */
    public String getAppointmentSchedule() {
        String period = "0".equals(this.timesArea) ? "上午" : "下午";
        return String.format("%s (%s) - %s", this.times, this.week, period);
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    /**
     * 验证就诊后的核心信息（如医嘱）是否已被记录。
     * @return 如果医嘱(advice)字段已经填写，则返回true，否则返回false。
     */
    public boolean isVisitRecordComplete() {
        return this.advice != null && !this.advice.trim().isEmpty();
    }

}