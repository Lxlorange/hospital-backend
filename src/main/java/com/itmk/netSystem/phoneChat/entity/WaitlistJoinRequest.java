package com.itmk.netSystem.phoneChat.entity;

import lombok.Data;

@Data
public class WaitlistJoinRequest {
    private Integer scheduleId;
    private Integer doctorId;
    private Integer userId;
    private Integer visitUserId;
}