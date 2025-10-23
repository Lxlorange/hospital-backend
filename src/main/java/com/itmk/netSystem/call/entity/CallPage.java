package com.itmk.netSystem.call.entity;
import lombok.Data;


@Data
public class CallPage {

    private Long currentPage;

    private Long pageSize;

    private Integer userId;
    private Long doctorId;
    private String name;
    private String timesArea;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasNameFilter() {
        return this.name != null && !this.name.isEmpty();
    }

    public boolean isDoctorQuery() {
        return this.doctorId != null && this.doctorId > 0;
    }

    public boolean isTimeAreaFilterApplied() {
        return this.timesArea != null && !this.timesArea.isEmpty();
    }

    public void normalizePageParams() {
        if (this.currentPage == null || this.currentPage < 1) {
            this.currentPage = 1L;
        }
        if (this.pageSize == null || this.pageSize < 10) {
            this.pageSize = 10L;
        }
    }



}