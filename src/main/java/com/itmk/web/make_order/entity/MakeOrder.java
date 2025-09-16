package com.itmk.web.make_order.entity;
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
    /** 预约id */
    @TableId(type = IdType.AUTO)
    private Integer makeId;

    private Integer scheduleId;
    /** 预约人id */
    private Integer userId;

    /** 就诊人id */
    private Integer visitUserId;
    /** 医生id */
    private Integer doctorId;
    /** 预约时间 */
    private String times;
    /** 预约时段： 0：上午 1：下午 */
    private String timesArea;
    /** 星期 */
    private String week;
    /** 预约订单创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    /** 预约金额 */
    private BigDecimal price;
    /** 就诊地址 */
    private String address;
    /** 状态：1：已预约  2：取消预约 */
    private String status;
    /** 是否叫号 ：0：未叫号  1：已叫号 */
    private String hasCall;
    /** 是否就诊 ：0：未就诊  1：已就诊 */
    private String hasVisit;

    @TableField(exist = false)
    private String visitname;
    @TableField(exist = false)
    private String deptName;
    @TableField(exist = false)
    private String nickName;

}