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
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Integer scheduleId;
    /** 医生id */
    private Integer doctorId;
    /** 医生姓名 */
    private String doctorName;
    /** 日期 */
    private String times;
    /** 星期几 */
    private String week;
    /** 第几周 */
    private Integer witchWeek;
    /** 挂号数量 */
    private Integer amount;
    /**剩余号 */
    private Integer lastAmount;
    /** 1:上班  0：休息 */
    private String type;
    //科室id
    @TableField(exist = false)
    private Integer deptId;
    /** 科室名称 */
    @TableField(exist = false)
    private String deptName;
    /** 挂号费 */
    @TableField(exist = false)
    private BigDecimal price;
}