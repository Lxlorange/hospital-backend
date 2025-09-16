package com.itmk.web.wx_user.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 *  
 * 小程序用户表
 */
@Data
@TableName("wx_user")
public class WxUser implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 用户id */
    @TableId(type = IdType.AUTO)
    private Integer userId;
    /** 登录账号 */
    private String userName;
    /** 昵称 */
    private String nickName;
    /** 电话 */
    private String phone;
    /** 性别 */
    private String sex;
    /** 姓名 */
    private String name;
    /** 头像 */
    private String image;
    /** 1:启用 0：停用 */
    private boolean status = true;
    /** 密码 */
    private String password;

    private Date createTime;

}