package com.itmk.netSystem.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.auth.entity.IdentityAuthRequest;
import com.itmk.netSystem.auth.mapper.IdentityAuthRequestMapper;
import com.itmk.netSystem.auth.service.IdentityAuthService;
import com.itmk.netSystem.userPatientPhone.entity.WxUser;
import com.itmk.netSystem.userPatientPhone.service.UserPatientPhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class IdentityAuthServiceImpl extends ServiceImpl<IdentityAuthRequestMapper, IdentityAuthRequest> implements IdentityAuthService {
    @Autowired
    private UserPatientPhoneService userPatientPhoneService;

    @Override
    public boolean submit(Integer userId, String username, String userType, String cardNo, String cardFront, String cardBack) {
        Date now = new Date();
        IdentityAuthRequest req = new IdentityAuthRequest();
        req.setUserId(userId);
        req.setUsername(username);
        String t = userType;
        if (t != null) {
            String s = t.trim().toLowerCase();
            if ("student".equals(s) || "学生".equals(s)) {
                t = "学生";
            } else if ("teacher".equals(s) || "教师".equals(s) || "老师".equals(s)) {
                t = "教师";
            }
        }
        req.setType(t);
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
        IdentityAuthRequest existing = this.getById(requestId);
        if (existing == null) {
            return false;
        }
        IdentityAuthRequest update = new IdentityAuthRequest();
        update.setRequestId(requestId);
        update.setStatus("1");
        update.setReviewComment(reviewComment);
        update.setReviewerId(reviewerId);
        update.setReviewTime(new Date());
        update.setUpdateTime(new Date());
        boolean ok = this.updateById(update);
        if (!ok) {
            return false;
        }
        String identity = existing.getType();
        if (identity != null) {
            String s = identity.trim().toLowerCase();
            if ("student".equals(s) || "学生".equals(s)) {
                identity = "学生";
            } else if ("teacher".equals(s) || "教师".equals(s) || "老师".equals(s)) {
                identity = "教师";
            }
        }
        if (identity != null && !identity.trim().isEmpty()) {
            Integer userId = existing.getUserId();
            if (userId != null) {
                WxUser u = new WxUser();
                u.setUserId(userId);
                u.setIdentityStatus(identity);
                userPatientPhoneService.updateById(u);
            } else if (existing.getUsername() != null && !existing.getUsername().trim().isEmpty()) {
                WxUser found = userPatientPhoneService.findByUsername(existing.getUsername());
                if (found != null && found.getUserId() != null) {
                    WxUser u = new WxUser();
                    u.setUserId(found.getUserId());
                    u.setIdentityStatus(identity);
                    userPatientPhoneService.updateById(u);
                }
            }
        }
        return true;
    }

    @Override
    public boolean reject(Long requestId, String reviewComment, Long reviewerId) {
        IdentityAuthRequest existing = this.getById(requestId);
        IdentityAuthRequest update = new IdentityAuthRequest();
        update.setRequestId(requestId);
        update.setStatus("2");
        update.setReviewComment(reviewComment);
        update.setReviewerId(reviewerId);
        update.setReviewTime(new Date());
        update.setUpdateTime(new Date());
        boolean ok = this.updateById(update);
        if (!ok) {
            return false;
        }
        if (existing != null) {
            Integer userId = existing.getUserId();
            if (userId != null) {
                WxUser u = new WxUser();
                u.setUserId(userId);
                u.setIdentityStatus("待认证");
                userPatientPhoneService.updateById(u);
            } else if (existing.getUsername() != null && !existing.getUsername().trim().isEmpty()) {
                WxUser found = userPatientPhoneService.findByUsername(existing.getUsername());
                if (found != null && found.getUserId() != null) {
                    WxUser u = new WxUser();
                    u.setUserId(found.getUserId());
                    u.setIdentityStatus("待认证");
                    userPatientPhoneService.updateById(u);
                }
            }
        }
        return true;
    }
}