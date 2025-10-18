package com.itmk.netSystem.phoneChat.entity;

import lombok.Data;

 
@Data
public class ResetPasswordNum {
    private Integer userId;
    private String oldPassword;
    private String password;

    /**
     * 检查所有必要的字段是否都已填写。
     * @return 如果用户ID、旧密码和新密码都存在，则返回true。
     */
    public boolean hasAllFields() {
        return this.userId != null && isNotBlank(this.oldPassword) && isNotBlank(this.password);
    }

    /**
     * 检查新旧密码是否相同。
     * @return 如果新旧密码字符串相等，则返回true。
     */
    public boolean arePasswordsSame() {
        return this.oldPassword != null && this.oldPassword.equals(this.password);
    }

    /**
     * 对新密码进行简单的有效性验证（例如，长度大于6）。
     * @return 如果密码长度符合要求，则返回true。
     */
    public boolean isNewPasswordValid() {
        return this.password != null && this.password.length() >= 6;
    }

    /**
     * 出于安全考虑，清除内存中的密码信息。
     */
    public void clearPasswords() {
        this.oldPassword = null;
        this.password = null;
    }

    /**
     * 内部使用的非空字符串检查方法。
     * @param str 要检查的字符串
     * @return 如果字符串不为null且不为空，则返回true
     */
    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
