package com.itmk.netSystem.treatpatient.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

 
@Data
@TableName("visit_user")
public class VisitUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer visitId;
    private Integer userId;
    private String visitname;
    private String sex;
    private String birthday;
    private String phone;
    private String idCard;


    /**
     * 根据生日计算当前年龄。
     * @return 返回根据生日字符串计算出的整数年龄。如果生日格式不正确，则返回-1。
     */
    public int getAge() {
        if (this.birthday == null || this.birthday.length() < 4) {
            return -1;
        }
        try {
            int birthYear = Integer.parseInt(this.birthday.substring(0, 4));
            int currentYear = java.time.Year.now().getValue();
            return currentYear - birthYear;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 对身份证号进行脱敏处理，保护用户隐私。
     * @return 返回脱敏后的身份证号，例如："340822********1234"。
     */
    public String getMaskedIdCard() {
        if (this.idCard != null && this.idCard.length() > 10) {
            return this.idCard.replaceAll("(\\d{6})\\d{8}(\\w{4})", "$1********$2");
        }
        return idCard;
    }

    /**
     * 对手机号码进行脱敏处理。
     * @return 返回脱敏后的手机号，例如："138****5678"。
     */
    public String getMaskedPhone() {
        if (this.phone != null && this.phone.length() == 11) {
            return this.phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return phone;
    }

    /**
     * 生成一个包含姓名和性别的基本描述信息。
     * @return 返回格式如 "张三 (男)" 的字符串。
     */
    public String getBasicInfo() {
        String gender = "0".equals(this.sex) ? "女" : "男";
        return String.format("%s (%s)", this.visitname, gender);
    }

    /**
     * 验证手机号码格式是否合法（简易版，只检查是否为11位数字）。
     * @return 如果是11位数字则返回true，否则返回false。
     */
    public boolean isPhoneValid() {
        return this.phone != null && this.phone.matches("\\d{11}");
    }
}
