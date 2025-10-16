package com.itmk.netSystem.schedule.service.implement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.itmk.netSystem.schedule.entity.*;
import com.itmk.netSystem.schedule.mapper.*;
import com.itmk.netSystem.schedule.service.ScheduleService;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.userWeb.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    // 注入新创建的四个Mapper
    @Autowired private ScheduleTemplateMapper templateMapper;
    @Autowired private TemplateSlotMapper templateSlotMapper;
    @Autowired private ScheduleInstanceMapper instanceMapper;
    @Autowired private InstanceSlotMapper instanceSlotMapper;

    // 注入用于关联查询的Mapper
    @Autowired private com.itmk.netSystem.userWeb.mapper.userWebMapper sysUserMapper;
    @Autowired private com.itmk.netSystem.teamDepartment.mapper.teamDepartmentMapper departmentMapper;

    @Override
    public List<ScheduleTemplate> getDoctorTemplates(Long doctorId) {
        List<ScheduleTemplate> templates = templateMapper.selectList(
                new QueryWrapper<ScheduleTemplate>().eq("doctor_id", doctorId)
        );
        // 为每个模板查询并填充其关联的号源列表
        templates.forEach(template -> {
            List<TemplateSlot> slots = templateSlotMapper.selectList(
                    new QueryWrapper<TemplateSlot>().eq("template_id", template.getTemplateId())
            );
            template.setSlots(slots);
        });
        return templates;
    }

    @Override
    @Transactional
    public void saveDoctorTemplates(Long doctorId, List<Map<String, Object>> request) {
        // 1. 删除该医生的所有旧模板及关联的号源
        List<ScheduleTemplate> oldTemplates = templateMapper.selectList(
                new QueryWrapper<ScheduleTemplate>().eq("doctor_id", doctorId)
        );
        if (!oldTemplates.isEmpty()) {
            List<Long> oldTemplateIds = oldTemplates.stream()
                    .map(ScheduleTemplate::getTemplateId).collect(Collectors.toList());
            templateSlotMapper.delete(new QueryWrapper<TemplateSlot>().in("template_id", oldTemplateIds));
            templateMapper.deleteBatchIds(oldTemplateIds);
        }

        // 2. 插入新的模板数据
        for (Map<String, Object> reqMap : request) {
            ScheduleTemplate newTemplate = new ScheduleTemplate();
            newTemplate.setDoctorId(doctorId);
            newTemplate.setDayOfWeek((Integer) reqMap.get("dayOfWeek"));
            newTemplate.setTimeSlot((Integer) reqMap.get("timeSlot"));
            templateMapper.insert(newTemplate); // 插入后，MyBatis-Plus会自动回填templateId

            List<Map<String, Object>> slots = (List<Map<String, Object>>) reqMap.get("slots");
            for (Map<String, Object> slotMap : slots) {
                TemplateSlot newSlot = new TemplateSlot();
                newSlot.setTemplateId(newTemplate.getTemplateId());
                newSlot.setSlotType((String) slotMap.get("slotType"));
                newSlot.setTotalAmount((Integer) slotMap.get("totalAmount"));
                // 做类型转换以确保安全
                newSlot.setPrice(new BigDecimal(String.valueOf(slotMap.get("price"))));
                templateSlotMapper.insert(newSlot);
            }
        }
    }

    @Override
    @Transactional
    public void generateInstances(Map<String, String> request) {
        LocalDate startDate = LocalDate.parse(request.get("startDate"));
        LocalDate endDate = LocalDate.parse(request.get("endDate"));
        List<ScheduleTemplate> allTemplates = templateMapper.selectList(null);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final int dayOfWeek = date.getDayOfWeek().getValue();
            final LocalDate currentDate = date;

            allTemplates.stream()
                    .filter(t -> t.getDayOfWeek() == dayOfWeek)
                    .forEach(template -> {
                        // 检查是否已存在排班实例，若存在则跳过
                        boolean exists = instanceMapper.exists(new QueryWrapper<ScheduleInstance>()
                                .eq("doctor_id", template.getDoctorId())
                                .eq("schedule_date", currentDate)
                                .eq("time_slot", template.getTimeSlot())
                        );

                        if (!exists) {
                            ScheduleInstance instance = new ScheduleInstance();
                            instance.setDoctorId(template.getDoctorId());
                            instance.setScheduleDate(currentDate);
                            instance.setTimeSlot(template.getTimeSlot());
                            instance.setStatus(1); // 默认状态为“正常”
                            instanceMapper.insert(instance);

                            List<TemplateSlot> templateSlots = templateSlotMapper.selectList(
                                    new QueryWrapper<TemplateSlot>().eq("template_id", template.getTemplateId())
                            );
                            for (TemplateSlot ts : templateSlots) {
                                InstanceSlot is = new InstanceSlot();
                                is.setInstanceId(instance.getInstanceId());
                                is.setSlotType(ts.getSlotType());
                                is.setPrice(ts.getPrice());
                                is.setTotalAmount(ts.getTotalAmount());
                                is.setAvailableAmount(ts.getTotalAmount()); // 初始可用数等于总数
                                instanceSlotMapper.insert(is);
                            }
                        }
                    });
        }
    }

    @Override
    public List<ScheduleInstance> findInstances(String startDate, String endDate, Long deptId, Long doctorId) {
        // 使用 mybatis-plus-join 进行连表查询
        MPJLambdaWrapper<ScheduleInstance> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.between(ScheduleInstance::getScheduleDate, startDate, endDate)
                .leftJoin(SysUser.class, SysUser::getUserId, ScheduleInstance::getDoctorId)
                .leftJoin(Department.class, Department::getDeptId, SysUser::getDeptId)
                .selectAll(ScheduleInstance.class)
                // 连表查询医生和科室名称，并设置别名以匹配实体类的临时字段
                .selectAs(SysUser::getNickName, ScheduleInstance::getDoctorName)
                .selectAs(Department::getDeptName, ScheduleInstance::getDepartmentName);

        if (doctorId != null) {
            queryWrapper.eq(ScheduleInstance::getDoctorId, doctorId);
        }
        if (deptId != null) {
            queryWrapper.eq(SysUser::getDeptId, deptId);
        }

        // 先查询出主实例列表
        List<ScheduleInstance> instances = instanceMapper.selectJoinList(ScheduleInstance.class, queryWrapper);

        // 遍历查询每个实例的号源详情 (N+1查询，数据量大时建议优化)
        instances.forEach(instance -> {
            List<InstanceSlot> slots = instanceSlotMapper.selectList(
                    new QueryWrapper<InstanceSlot>().eq("instance_id", instance.getInstanceId())
            );
            instance.setSlots(slots);
        });

        return instances;
    }

    @Override
    public void updateInstanceStatus(Long instanceId, Map<String, Integer> request) {
        Integer status = request.get("status");
        if (status != null) {
            ScheduleInstance instance = new ScheduleInstance();
            instance.setInstanceId(instanceId);
            instance.setStatus(status);
            instanceMapper.updateById(instance);
        }
    }
}