package com.itmk.netSystem.teamDepartment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

 
@Data
@TableName("department")
public class Department {
    public Department(Integer deptId, String deptName, String phone, Integer orderNum) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.phone = phone;
        this.orderNum = orderNum;
    }

    public Department() {
    }

    @TableId(type = IdType.AUTO)
    private Integer deptId;
    private String deptName;
    private String phone;

    private String toHome = "0";
    private Integer orderNum;
}
