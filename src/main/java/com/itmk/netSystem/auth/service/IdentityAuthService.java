package com.itmk.netSystem.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.auth.entity.IdentityAuthRequest;

/**
 * 身份认证服务
 */
public interface IdentityAuthService extends IService<IdentityAuthRequest> {
    /**
     * 提交身份认证申请，默认状态为0（待审核）
     */
    boolean submit(Integer userId, String username, String userType, String cardNo, String cardFront, String cardBack);

    /**
     * 审核通过
     */
    boolean approve(Long requestId, String reviewComment, Long reviewerId);

    /**
     * 审核拒绝
     */
    boolean reject(Long requestId, String reviewComment, Long reviewerId);
}