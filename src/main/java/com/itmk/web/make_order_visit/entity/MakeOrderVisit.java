package com.itmk.web.make_order_visit.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("make_order_visit")
public class MakeOrderVisit implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Integer visitId;
    /** 预约id */
    private Integer makeId;
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
    /** 是否就诊：0：未就诊 1：已就诊 */
    private String hasVisit;
    /** 是否需要住院：0：不需要 1：需要 */
    private String hasLive;
    /** 医嘱、诊断说明 */
    private String advice;
    /** 就诊时间 */
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

}