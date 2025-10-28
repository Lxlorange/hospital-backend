package com.itmk.netSystem.plat.entity;

import lombok.Data;

 
@Data
public class Plat {
    private long departmentCount;
    private long sysUserCount;
    private long wxUserCount;
    private long visitCount;

    /**
     * @description: 计算平台的用户总数（医生 + 患者）。
     * @return 返回系统用户和微信用户的总和。
     */
    public long getTotalUserCount() {
        return this.sysUserCount + this.wxUserCount;
    }

    /**
     * @description: 计算每个医生平均对应的患者数量。
     * @return 返回患者与医生的比例。如果医生数量为0，则返回0.0以避免除零错误。
     */
    public double calculatePatientToDoctorRatio() {
        if (this.sysUserCount == 0) {
            return 0.0;
        }
        // 将其中一个操作数转换为double以确保结果是浮点数
        return (double) this.wxUserCount / this.sysUserCount;
    }

    /**
     * @description: 计算平均每个患者的就诊次数。
     * @return 返回总就诊次数与患者总数的比率。如果患者数为0，则返回0.0。
     */
    public double getAverageVisitsPerPatient() {
        if (this.wxUserCount == 0) {
            return 0.0;
        }
        return (double) this.visitCount / this.wxUserCount;
    }

    /**
     * @description: 生成一段描述性的摘要文本，方便日志记录或前端展示。
     * @return 返回格式化的平台统计摘要字符串。
     */
    public String toSummaryString() {
        return String.format(
                "平台统计摘要: 科室数=%d, 医生数=%d, 患者数=%d, 总就诊次数=%d",
                this.departmentCount,
                this.sysUserCount,
                this.wxUserCount,
                this.visitCount
        );
    }

    /**
     * @description: 检查平台是否已有基础数据（至少有一个科室和一名医生）。
     * @return 如果平台已录入科室和医生数据，则返回 true，否则返回 false。
     */
    public boolean isPlatformInitialized() {
        return this.departmentCount > 0 && this.sysUserCount > 0;
    }
}