package com.itmk.netSystem.evaluate.entity;
import lombok.Data;

 
@Data
public class Evaluate {
    private Long currentPage;
    private Long pageSize;
    private String name;

    /**
     * 检查是否存在名称搜索条件
     * @return boolean
     */
    public boolean hasNameFilter(){
        // 使用 org.apache.commons.lang.StringUtils 保持一致性
        return org.apache.commons.lang3.StringUtils.isNotEmpty(this.name);
    }

    /**
     * 清除名称搜索条件
     */
    public void clearNameFilter(){
        this.name = null;
    }

    /**
     * 获取分页信息的字符串表示，用于日志记录
     * @return String
     */
    public String getPagingInfo(){
        return "Page: " + this.currentPage + ", Size: " + this.pageSize;
    }
}