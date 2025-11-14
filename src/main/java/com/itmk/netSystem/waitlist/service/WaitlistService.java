package com.itmk.netSystem.waitlist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.waitlist.entity.WaitlistEntry;

import java.util.List;

public interface WaitlistService extends IService<WaitlistEntry> {
    /** 加入候补队列 */
    boolean joinWaitlist(Integer scheduleId, Integer doctorId, Integer userId, Integer visitUserId);

    /** 为指定排班分配候补（如有余量则自动创建预约） */
    boolean allocateFromWaitlistForSchedule(Integer scheduleId);

    /** 查询指定排班下待候补的前N条记录 */
    List<WaitlistEntry> listPendingBySchedule(Integer scheduleId, int limit);
}