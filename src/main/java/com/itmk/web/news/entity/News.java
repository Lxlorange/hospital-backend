package com.itmk.web.news.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("news")
public class News implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 标题 */
    private String title;
    /** 图片 */
    private String image;
    /** 简介 */
    private String textDesc;
    /** 内容 */
    private String textContent;
    /** 设置到首页 0：否 1：是 */
    private String toIndex;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

}