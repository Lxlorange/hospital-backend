package com.itmk.netSystem.phoneChat.entity;

import lombok.Data;

 
@Data
public class DoctorInformationNum {
    private String doctorId;
    private String startDate;

    /**
     * 检查查询所必需的参数是否都已提供。
     * @return 如果医生ID和开始日期都不为空，则返回true。
     */
    public boolean hasRequiredParameters() {
        return this.doctorId != null && !this.doctorId.trim().isEmpty() &&
                this.startDate != null && !this.startDate.trim().isEmpty();
    }

    /**
     * 将医生ID从字符串转换为Long类型。
     * @return Long类型的医生ID，如果转换失败则返回null。
     */
    public Long getDoctorIdAsLong() {
        try {
            return Long.parseLong(this.doctorId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 将开始日期字符串转换为LocalDate对象。
     * @return LocalDate对象，如果格式不正确则返回null。
     */
    public java.time.LocalDate getStartDateAsLocalDate() {
        try {
            return java.time.LocalDate.parse(this.startDate, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (java.time.format.DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 检查查询的日期是否是今天。
     * @return 如果开始日期是今天的日期，则返回true。
     */
    public boolean isQueryForToday() {
        java.time.LocalDate queryDate = getStartDateAsLocalDate();
        return queryDate != null && queryDate.equals(java.time.LocalDate.now());
    }

    /**
     * 清空所有查询参数。
     */
    public void clear() {
        this.doctorId = null;
        this.startDate = null;
    }
}
