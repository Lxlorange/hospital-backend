package com.itmk.netSystem.LLM.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("llmstore")
public class LLMstore {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField
    private String prompt;
    @TableField
    private String message;
}
