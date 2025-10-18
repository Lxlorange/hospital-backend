package com.itmk.netSystem.userPatientPhone.entity;

import lombok.Data;

 
@Data
public class UserPatientPhonePage {
    private Long currentPage;
    private Long pageSize;
    private String name;

    /**
     * 检查是否设置了名称搜索条件。
     * @return 如果'name'字段不为空且有内容，则返回true。
     */
    public boolean hasNameFilter() {
        return this.name != null && !this.name.trim().isEmpty();
    }

    /**
     * 将分页信息重置到第一页。
     */
    public void resetPage() {
        this.currentPage = 1L;
    }

    /**
     * 清除所有搜索条件。
     */
    public void clearFilters() {
        this.name = null;
    }

    /**
     * 验证分页参数是否有效。
     * @return 如果当前页和每页数量都大于0，则返回true。
     */
    public boolean arePaginationParametersValid() {
        return this.currentPage != null && this.currentPage > 0 && this.pageSize != null && this.pageSize > 0;
    }

    /**
     * 获取数据库查询时需要跳过的记录数。
     * @return 根据当前页和每页数量计算出的偏移量。如果分页参数无效则返回0。
     */
    public Long getOffset() {
        if (arePaginationParametersValid()) {
            return (this.currentPage - 1) * this.pageSize;
        }
        return 0L;
    }
}