package com.itmk.web.phone.entity;

import lombok.Data;

import java.math.BigDecimal;

 
@Data
public class UserDesc {
    private String nickName;
    private String sex;
    private String jobTitle;
    private String image;
    private Long userId;
    private String deptName;
    private String visitAddress;

    private String goodAt;

    private BigDecimal price;

    private String introduction;
}
