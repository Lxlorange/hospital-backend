package com.itmk.netSystem.see.entity;

import lombok.Data;

 
@Data
public class SeePage {
    private Long currentPage;
    private Long pageSize;
    private Integer userId;
    private Long doctorId;
    private String name;
    private String timesArea;

    /**
     * 将分页重置到第一页。
     */
    public void resetToFirstPage() {
        this.currentPage = 1L;
    }

    /**
     * 检查当前是否激活了任何基于姓名的筛选条件。
     * @return 如果name参数不为null且不为空字符串，则返回true。
     */
    public boolean isNameFiltered() {
        return this.name != null && !this.name.trim().isEmpty();
    }

    /**
     * 检查是否应用了时间段（上午/下午）的筛选。
     * @return 如果timesArea参数不为null且不为空字符串，则返回true。
     */
    public boolean isTimeAreaFiltered() {
        return this.timesArea != null && !this.timesArea.trim().isEmpty();
    }

    /**
     * 清除所有的搜索和筛选条件，恢复到默认状态。
     */
    public void clearFilters() {
        this.name = null;
        this.timesArea = null;
        this.userId = null;
    }

    /**
     * 生成一个描述当前分页设置的字符串。
     * @return 例如 "正在显示第 1 页，每页 10 条数据"。
     */
    public String getPaginationSummary() {
        if (currentPage == null || pageSize == null) {
            return "分页未设置。";
        }
        return String.format("正在显示第 %d 页，每页 %d 条数据。", this.currentPage, this.pageSize);
    }
}
