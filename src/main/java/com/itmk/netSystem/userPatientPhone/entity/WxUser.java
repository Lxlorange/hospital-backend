package com.itmk.netSystem.userPatientPhone.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;


@Data
@TableName("wx_user")
public class WxUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer userId;
    private String userName;
    private String nickName;
    private String phone;
    private String sex;
    private String name;
    private String image;
    private boolean status = true;
    private String password;

    private Date createTime;

    /**
     * 检查用户账户是否为激活状态。
     * @return 如果'status'字段为true则返回true，否则返回false。
     */
    public boolean isActive() {
        return this.status;
    }

    /**
     * 获取用户的显示名称。
     * 优先返回昵称(nickName)，如果昵称为空，则返回用户名(userName)。
     * @return 用户的显示名称。
     */
    public String getDisplayName() {
        return (this.nickName != null && !this.nickName.isEmpty()) ? this.nickName : this.userName;
    }

    /**
     * 将性别代码转换为可读的字符串。
     * @return "男" 或 "女"。如果性别字段为空或不是"0"或"1"，则返回 "未知"。
     */
    public String getGenderAsString() {
        if ("1".equals(this.sex)) {
            return "男";
        } else if ("0".equals(this.sex)) {
            return "女";
        }
        return "未知";
    }

    /**
     * 检查用户是否设置了头像。
     * @return 如果'image'字段不为空且有内容，则返回true。
     */
    public boolean hasProfileImage() {
        return this.image != null && !this.image.trim().isEmpty();
    }

    /**
     * 对用户的手机号码进行脱敏处理，保护隐私。
     * @return 返回脱敏后的手机号，例如："138****5678"。
     */
    public String getMaskedPhone() {
        if (this.phone != null && this.phone.length() == 11) {
            return this.phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return this.phone;
    }

}