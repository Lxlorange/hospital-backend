package com.itmk.netSystem.waitlist.service.implement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.call.entity.MakeOrder;
import com.itmk.netSystem.call.service.CallService;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.service.setWorkService;
import com.itmk.netSystem.userPatientPhone.entity.WxUser;
import com.itmk.netSystem.userPatientPhone.service.UserPatientPhoneService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.netSystem.waitlist.entity.WaitlistEntry;
import com.itmk.netSystem.waitlist.mapper.WaitlistMapper;
import com.itmk.netSystem.waitlist.service.WaitlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class WaitlistServiceImpl extends ServiceImpl<WaitlistMapper, WaitlistEntry> implements WaitlistService {

    @Autowired
    private setWorkService setWorkService;
    @Autowired
    private CallService callService;
    @Autowired
    private UserPatientPhoneService userPatientPhoneService;
    @Autowired
    private userWebService userWebService;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public boolean joinWaitlist(Integer scheduleId, Integer doctorId, Integer userId, Integer visitUserId) {
        // 防重复：同就诊人同排班存在待候补记录时不重复加入
        QueryWrapper<WaitlistEntry> dup = new QueryWrapper<>();
        dup.lambda()
                .eq(WaitlistEntry::getScheduleId, scheduleId)
                .eq(WaitlistEntry::getVisitUserId, visitUserId)
                .eq(WaitlistEntry::getStatus, "pending");
        if (this.count(dup) > 0) {
            return true; // 已在候补队列中，视为成功
        }

        // 防止该就诊人已预约此排班
        QueryWrapper<MakeOrder> dupOrder = new QueryWrapper<>();
        dupOrder.lambda()
                .eq(MakeOrder::getVisitUserId, visitUserId)
                .eq(MakeOrder::getScheduleId, scheduleId)
                .eq(MakeOrder::getStatus, "1");
        if (callService.count(dupOrder) > 0) {
            return false; // 已有预约，无需候补
        }

        WaitlistEntry entry = new WaitlistEntry();
        entry.setScheduleId(scheduleId);
        entry.setDoctorId(doctorId);
        entry.setUserId(userId);
        entry.setVisitUserId(visitUserId);
        entry.setStatus("pending");
        entry.setPriority(0);
        entry.setCreateTime(new Date());
        entry.setUpdateTime(new Date());
        return this.save(entry);
    }

    @Override
    public List<WaitlistEntry> listPendingBySchedule(Integer scheduleId, int limit) {
        QueryWrapper<WaitlistEntry> qw = new QueryWrapper<>();
        qw.lambda()
                .eq(WaitlistEntry::getScheduleId, scheduleId)
                .eq(WaitlistEntry::getStatus, "pending")
                .orderByAsc(WaitlistEntry::getPriority)
                .orderByAsc(WaitlistEntry::getCreateTime)
                .last("LIMIT " + limit);
        return this.list(qw);
    }

    @Override
    @Transactional
    public boolean allocateFromWaitlistForSchedule(Integer scheduleId) {
        // 行锁获取排班，确保并发安全
        QueryWrapper<ScheduleDetail> lockQ = new QueryWrapper<>();
        lockQ.lambda().eq(ScheduleDetail::getScheduleId, scheduleId).last("for update");
        ScheduleDetail schedule = setWorkService.getOne(lockQ);
        if (schedule == null || schedule.getLastAmount() == null || schedule.getLastAmount() <= 0) {
            return false; // 无余号
        }

        // 取一条候补
        List<WaitlistEntry> candidates = listPendingBySchedule(scheduleId, 1);
        if (candidates == null || candidates.isEmpty()) {
            return false; // 无候补
        }
        WaitlistEntry candidate = candidates.get(0);

        // 防止重复预约
        QueryWrapper<MakeOrder> dupOrder = new QueryWrapper<>();
        dupOrder.lambda()
                .eq(MakeOrder::getVisitUserId, candidate.getVisitUserId())
                .eq(MakeOrder::getScheduleId, scheduleId)
                .eq(MakeOrder::getStatus, "1");
        if (callService.count(dupOrder) > 0) {
            // 候补患者已预约，直接标记为 allocated 并跳过
            candidate.setStatus("allocated");
            candidate.setUpdateTime(new Date());
            this.updateById(candidate);
            return false;
        }

        // 创建预约订单
        MakeOrder order = new MakeOrder();
        order.setScheduleId(schedule.getScheduleId());
        order.setDoctorId(schedule.getDoctorId());
        order.setUserId(candidate.getUserId());
        order.setVisitUserId(candidate.getVisitUserId());
        order.setCreateTime(new Date());
        order.setStatus("1");
        order.setHasVisit("0");
        order.setHasCall("0");
        order.setPrice(schedule.getPrice());
        try {
            String dateStr = schedule.getTimes() == null ? null : schedule.getTimes().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            order.setTimes(dateStr);
        } catch (Exception ignored) {}
        if (schedule.getTimeSlot() != null) {
            order.setTimesArea(String.valueOf(schedule.getTimeSlot()));
        }
        order.setWeek(schedule.getWeek());

        if (!callService.save(order)) {
            return false;
        }

        // 余号减一
        setWorkService.subCount(scheduleId);

        // 更新候补为已分配
        candidate.setStatus("allocated");
        candidate.setUpdateTime(new Date());
        this.updateById(candidate);

        // 发送通知邮件
        sendAllocationEmail(order);

        return true;
    }

    private void sendAllocationEmail(MakeOrder order) {
        try {
            if (order.getUserId() == null) return;
            WxUser wxUser = userPatientPhoneService.getById(order.getUserId());
            if (wxUser == null || !StringUtils.hasText(wxUser.getEmail())) return;

            String toEmail = wxUser.getEmail();
            String doctorName = "";
            if (order.getDoctorId() != null) {
                SysUser doctor = userWebService.getById(order.getDoctorId());
                if (doctor != null) doctorName = doctor.getNickName();
            }

            String timesAreaLabel = "";
            try {
                String ta = order.getTimesArea();
                if ("0".equals(ta)) timesAreaLabel = "上午";
                else if ("1".equals(ta)) timesAreaLabel = "下午";
            } catch (Exception ignored) {}

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("18201500146@163.com");
            message.setTo(toEmail);
            message.setSubject("候补成功通知");
            StringBuilder sb = new StringBuilder();
            sb.append("尊敬的用户，您好！\n\n");
            sb.append("您已通过候补获得新的挂号：\n");
            if (StringUtils.hasText(doctorName)) sb.append("医生：").append(doctorName).append("\n");
            if (StringUtils.hasText(order.getTimes())) {
                sb.append("就诊日期：").append(order.getTimes());
                if (StringUtils.hasText(timesAreaLabel)) sb.append(" （").append(timesAreaLabel).append("）");
                sb.append("\n");
            }
            sb.append("订单号：").append(order.getMakeId()).append("\n\n");
            sb.append("请按预约时间前来就诊，祝您健康！");
            message.setText(sb.toString());
            mailSender.send(message);
        } catch (Exception ignored) {}
    }
}