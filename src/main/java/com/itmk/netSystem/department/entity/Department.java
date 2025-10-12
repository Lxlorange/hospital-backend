package com.itmk.netSystem.department.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

 
@Data
@TableName("department")
public class Department {
    //主键，自动递增
    @TableId(type = IdType.AUTO)
    private Integer deptId;
    private String deptName;
    private String phone;
    /** 是否推荐到首页 0：未推荐  1：推荐 */
    private String toHome = "0";
    private Integer orderNum;
}
