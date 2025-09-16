package com.itmk.web.suggest.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("suggest")
public class Suggest implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 反馈人id */
    private Integer userId;
    /** 标题 */
    private String title;
    /** 反馈内容 */
    private String content;
    /** 反馈时间 */
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    @TableField(exist = false)
    private String nickName;
    @TableField(exist = false)
    private String image;

}