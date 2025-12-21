package com.itmk.netSystem.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itmk.netSystem.call.entity.MakeOrder;
import com.itmk.netSystem.call.service.CallService; // 对应你的 callService
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService; // 对应你的 userWebService
import com.itmk.netSystem.userPatientPhone.entity.WxUser;
import com.itmk.netSystem.userPatientPhone.service.UserPatientPhoneService; // 对应你的 userPatientPhoneService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Component
public class AppointmentReminderTask {

    @Autowired
    private CallService callService; // 预约服务
    @Autowired
    private UserPatientPhoneService userPatientPhoneService; // 用户服务
    @Autowired
    private userWebService userWebService; // 医生服务
    @Autowired
    private JavaMailSender mailSender;

    // 发件人邮箱
    private String fromEmail = "18201500146@163.com";

    /**
     * 每天早上 8:00 执行一次提醒（时间可以更换）
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendTomorrowReminders() {
        System.out.println("=== 开始执行明日就诊提醒任务 ===");
        String tomorrow = LocalDate.now().plusDays(1).toString();

        LambdaQueryWrapper<MakeOrder> query = new LambdaQueryWrapper<>();
        query.eq(MakeOrder::getTimes, tomorrow)
                .eq(MakeOrder::getStatus, "1")
                .eq(MakeOrder::getHasVisit, "0");

        List<MakeOrder> orders = callService.list(query);

        if (orders.isEmpty()) {
            System.out.println("明日无预约记录，无需发送提醒。");
            return;
        }

        for (MakeOrder order : orders) {
            try {
                // 获取用户信信息
                WxUser wxUser = userPatientPhoneService.getById(order.getUserId());
                if (wxUser == null || !StringUtils.hasText(wxUser.getEmail())) {
                    continue;
                }

                //获取医生信息
                String doctorName = "普通医师";
                if (order.getDoctorId() != null) {
                    SysUser doctor = userWebService.getById(order.getDoctorId());
                    if (doctor != null) {
                        doctorName = doctor.getNickName();
                    }
                }

                //处理时段显示
                String timesAreaLabel = "";
                if ("0".equals(order.getTimesArea())) {
                    timesAreaLabel = "上午";
                } else if ("1".equals(order.getTimesArea())) {
                    timesAreaLabel = "下午";
                }

                //构建邮件内容
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(wxUser.getEmail());
                message.setSubject("【就诊提醒】您明天的预约");

                StringBuilder sb = new StringBuilder();
                sb.append("尊敬的患者，您好！\n\n");
                sb.append("温馨提示：您预约了明天的门诊，请准时前往医院就诊。\n");
                sb.append("----------------------------------\n");
                sb.append("就诊日期：").append(order.getTimes()).append("\n");
                sb.append("就诊时段：").append(timesAreaLabel).append("\n");
                sb.append("预约医生：").append(doctorName).append("\n");
                sb.append("就诊地址：").append(order.getAddress()).append("\n");
                sb.append("----------------------------------\n\n");
                sb.append("请携带好身份证及相关病历资料。如无法按时就诊，请提前在系统中取消预约。");

                message.setText(sb.toString());

                mailSender.send(message);
                System.out.println("提醒邮件已发送至：" + wxUser.getEmail());

            } catch (Exception e) {
                System.err.println("订单 " + order.getMakeId() + " 发送提醒失败：" + e.getMessage());
            }
        }

        System.out.println("=== 明日就诊提醒任务执行结束 ===");
    }
}