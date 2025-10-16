package com.itmk.netSystem.schedule.service;

import com.itmk.netSystem.schedule.entity.ScheduleInstance;
import com.itmk.netSystem.schedule.entity.ScheduleTemplate;

import java.util.List;
import java.util.Map;

/**
 * 新版排班（模板与实例）服务接口
 */
public interface ScheduleService {

    /**
     * 3.1 获取指定医生的所有排班模板记录
     * @param doctorId 医生ID
     * @return 包含号源详情的模板列表
     */
    List<ScheduleTemplate> getDoctorTemplates(Long doctorId);

    /**
     * 3.2 整体替换保存一个医生的排班模板
     * @param doctorId 医生ID
     * @param request 请求体，结构为 List<Map<String, Object>>
     */
    void saveDoctorTemplates(Long doctorId, List<Map<String, Object>> request);

    /**
     * 3.3 根据模板在指定时间范围内生成具体的排班实例
     * @param request 请求体，包含 startDate 和 endDate
     */
    void generateInstances(Map<String, String> request);

    /**
     * 3.4 根据条件查询已生成的排班实例
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param deptId 科室ID (可选)
     * @param doctorId 医生ID (可选)
     * @return 包含医生、科室、号源详情的排班实例列表
     */
    List<ScheduleInstance> findInstances(String startDate, String endDate, Long deptId, Long doctorId);

    /**
     * 3.5 更新排班实例状态（停诊/复诊）
     * @param instanceId 实例ID
     * @param request 请求体，包含 status
     */
    void updateInstanceStatus(Long instanceId, Map<String, Integer> request);
}