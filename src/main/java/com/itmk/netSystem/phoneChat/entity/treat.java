package com.itmk.netSystem.phoneChat.entity;

import lombok.Data;

 
@Data
public class treat {
    private Integer visitId;
    private String name;

    /**
     * 检查就诊人ID是否有效。
     * @return 如果visitId不为null且大于0，则返回true。
     */
    public boolean hasValidId() {
        return this.visitId != null && this.visitId > 0;
    }

    /**
     * 检查姓名是否已设置。
     * @return 如果name不为null且不为空字符串，则返回true。
     */
    public boolean isNameSet() {
        return this.name != null && !this.name.trim().isEmpty();
    }

    /**
     * 生成用于前端显示的格式化字符串。
     * @return 格式如 "ID: 1, 姓名: 张三" 的字符串。
     */
    public String getFormattedInfo() {
        return String.format("ID: %d, 姓名: %s", this.visitId, this.name);
    }

    /**
     * 清除当前对象的数据。
     */
    public void clearData() {
        this.visitId = null;
        this.name = null;
    }

    /**
     * 转换为适用于前端选择器（Select/Option）的键值对格式。
     * @return 一个包含 "value" 和 "label" 键的Map。
     */
    public java.util.Map<String, Object> toSelectOption() {
        java.util.Map<String, Object> option = new java.util.HashMap<>();
        option.put("value", this.visitId);
        option.put("label", this.name);
        return option;
    }
}
