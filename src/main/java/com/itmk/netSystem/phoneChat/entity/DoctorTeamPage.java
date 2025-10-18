package com.itmk.netSystem.phoneChat.entity;

import lombok.Data;

 
@Data
public class DoctorTeamPage {
    private Integer deptId;
    private Long currentPage;
    private Long pageSize;

    /**
     * 检查是否设置了科室筛选条件。
     * @return 如果deptId不为null，则返回true。
     */
    public boolean isDepartmentFiltered() {
        return this.deptId != null;
    }

    /**
     * 将分页信息重置到第一页。
     */
    public void resetToFirstPage() {
        this.currentPage = 1L;
    }

    /**
     * 验证分页参数是否有效。
     * @return 如果当前页和每页数量都大于0，则返回true。
     */
    public boolean arePaginationParamsValid() {
        return this.currentPage != null && this.currentPage > 0 && this.pageSize != null && this.pageSize > 0;
    }

    /**
     * 根据当前页和每页数量计算数据库查询的偏移量。
     * @return 数据库查询应跳过的记录数。
     */
    public long getOffset() {
        if (arePaginationParamsValid()) {
            return (this.currentPage - 1) * this.pageSize;
        }
        return 0L;
    }

    /**
     * 清除科室筛选条件。
     */
    public void clearDepartmentFilter() {
        this.deptId = null;
    }
}
