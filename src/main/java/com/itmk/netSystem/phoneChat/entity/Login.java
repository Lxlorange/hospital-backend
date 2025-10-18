package com.itmk.netSystem.phoneChat.entity;

import lombok.Data;

 
@Data
public class Login {
    private Integer userId;

    /**
     * 检查用户ID是否存在。
     * @return 如果userId不为null，则返回true。
     */
    public boolean hasUserId() {
        return this.userId != null;
    }

    /**
     * 将用户ID转换为字符串格式。
     * @return 用户ID的字符串表示形式。
     */
    public String getUserIdAsString() {
        return this.userId != null ? String.valueOf(this.userId) : null;
    }

    /**
     * 将用户ID重置为null。
     */
    public void resetUserId() {
        this.userId = null;
    }

    /**
     * 比较传入的ID是否与当前用户ID相同。
     * @param otherUserId 另一个用户ID。
     * @return 如果相同则返回true。
     */
    public boolean isSameUser(Integer otherUserId) {
        return this.userId != null && this.userId.equals(otherUserId);
    }

    /**
     * 生成一个可用于JWT负载的简单Map。
     * @return 包含用户ID的Map。
     */
    public java.util.Map<String, Object> toJwtPayload() {
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("userId", this.userId);
        return payload;
    }
}
