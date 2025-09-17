package com.itmk.web.userPatient.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

 
@Data
@TableName("visit_user")
public class VisitUser implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 就诊人id */
    @TableId(type = IdType.AUTO)
    private Integer visitId;
    /** 绑定人id */
    private Integer userId;
    /** 姓名 */
    private String visitname;
    /** 性别： 0：男 1：女 */
    private String sex;
    /** 生日 */
    private String birthday;
    /** 就诊人电话 */
    private String phone;
    /** 身份证号码 */
    private String idCard;

}
