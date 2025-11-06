package com.itmk.netSystem.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.auth.entity.IdentityAuthRequest;
import com.itmk.netSystem.auth.mapper.IdentityAuthRequestMapper;
import com.itmk.netSystem.auth.service.IdentityAuthService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class IdentityAuthServiceImpl extends ServiceImpl<IdentityAuthRequestMapper, IdentityAuthRequest> implements IdentityAuthService {

    @Override
    public boolean submit(Integer userId, String username, String userType, String cardNo, String cardFront, String cardBack) {
        Date now = new Date();
        IdentityAuthRequest req = new IdentityAuthRequest();
        req.setUserId(userId);
        req.setUsername(username);
        req.setType(userType);
        req.setCode(cardNo);
        req.setFrontPhoto(cardFront);
        req.setBackPhoto(cardBack);
        req.setStatus("0");
        req.setCreateTime(now);
        req.setUpdateTime(now);
        return this.save(req);
    }

    @Override
    public boolean approve(Long requestId, String reviewComment, Long reviewerId) {
        IdentityAuthRequest update = new IdentityAuthRequest();
        update.setRequestId(requestId);
        update.setStatus("1");
        update.setReviewComment(reviewComment);
        update.setReviewerId(reviewerId);
        update.setReviewTime(new Date());
        update.setUpdateTime(new Date());
        return this.updateById(update);
    }

    @Override
    public boolean reject(Long requestId, String reviewComment, Long reviewerId) {
        IdentityAuthRequest update = new IdentityAuthRequest();
        update.setRequestId(requestId);
        update.setStatus("2");
        update.setReviewComment(reviewComment);
        update.setReviewerId(reviewerId);
        update.setReviewTime(new Date());
        update.setUpdateTime(new Date());
        return this.updateById(update);
    }
}