package com.itmk.netSystem.setWork.entity;

import lombok.Data;

 
@Data
public class setWorkList {
    private Long currentPage;
    private Long pageSize;
    private String doctorName;

    /**
     * 设置当前页码（链式方法）
     * @param currentPage 当前页码
     * @return 返回当前对象实例 (this)，以便进行链式调用
     */
    public setWorkList currentPage(Long currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    /**
     * 设置每页数量（链式方法）
     * @param pageSize 每页数量
     * @return 返回当前对象实例 (this)，以便进行链式调用
     */
    public setWorkList pageSize(Long pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * 设置医生姓名（链式方法）
     * @param doctorName 医生姓名
     * @return 返回当前对象实例 (this)，以便进行链式调用
     */
    public setWorkList doctorName(String doctorName) {
        this.doctorName = doctorName;
        return this;
    }
}
