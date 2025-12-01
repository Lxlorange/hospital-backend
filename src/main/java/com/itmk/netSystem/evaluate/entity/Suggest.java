package com.itmk.netSystem.evaluate.entity;

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
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    @TableField(exist = false)
    private String nickName;
    @TableField(exist = false)
    private String image;


    /**
     * 检查这是否是一个新的实体（尚未持久化，ID为空）
     * @return boolean
     */
    public boolean isNew() {
        return this.id == null;
    }

    /**
     * 更新创建时间为当前时间
     * (在执行新增操作前手动调用)
     */
    public void updateCreateTime() {
        this.createTime = new Date();
    }

    /**
     * 获取脱敏的昵称 (用于前端显示)
     * 假设 nickName 字段已被service层填充
     * @return String
     */
    public String getMaskedNickName() {
        if (this.nickName == null || this.nickName.length() <= 1) {
            return this.nickName;
        }
        if (this.nickName.length() == 2) {
            return this.nickName.substring(0, 1) + "*";
        }
        // 超过2个字符，保留首尾，中间用*
        return this.nickName.substring(0, 1) +
                "*" +
                this.nickName.substring(this.nickName.length() - 1);
    }
}