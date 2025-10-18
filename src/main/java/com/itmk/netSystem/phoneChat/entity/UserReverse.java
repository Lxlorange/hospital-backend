package com.itmk.netSystem.phoneChat.entity;

import lombok.Data;

import java.math.BigDecimal;

 
@Data
public class UserReverse {
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

    /**
     * 将性别代码转换为易于理解的文本。
     * @return "男"、"女" 或 "未知"。
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
     * 检查医生是否已上传头像。
     * @return 如果image字段不为空，则返回true。
     */
    public boolean hasImage() {
        return this.image != null && !this.image.trim().isEmpty();
    }

    /**
     * 获取医生的基本简介，如 "王医生 - 主任医师"。
     * @return 包含姓名和职称的字符串。
     */
    public String getProfileTitle() {
        return String.format("%s - %s", this.nickName, this.jobTitle);
    }

    /**
     * 检查是否设置了挂号费用。
     * @return 如果price不为null且大于0，则返回true。
     */
    public boolean isPriceSet() {
        return this.price != null && this.price.compareTo(java.math.BigDecimal.ZERO) > 0;
    }

    /**
     * 对医生姓名进行脱敏处理，例如 "王医生" 显示为 "王*生"。
     * @return 脱敏后的姓名。
     */
    public String getMaskedNickName() {
        if (this.nickName != null && this.nickName.length() > 2) {
            return this.nickName.charAt(0) + "*" + this.nickName.substring(this.nickName.length() - 1);
        }
        return this.nickName;
    }
}
