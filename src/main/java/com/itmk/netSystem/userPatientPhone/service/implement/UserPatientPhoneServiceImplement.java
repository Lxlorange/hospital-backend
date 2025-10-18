package com.itmk.netSystem.userPatientPhone.service.implement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.userPatientPhone.entity.WxUser;
import com.itmk.netSystem.userPatientPhone.mapper.UserPatientPhoneMapper;
import com.itmk.netSystem.userPatientPhone.service.UserPatientPhoneService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserPatientPhoneServiceImplement extends ServiceImpl<UserPatientPhoneMapper, WxUser> implements UserPatientPhoneService {
    @Override
    public WxUser findByPhone(String phone) {
        QueryWrapper<WxUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(WxUser::getPhone, phone);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public WxUser findByUsername(String username) {
        QueryWrapper<WxUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(WxUser::getUserName, username);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean register(WxUser newUser) {
        // 检查手机号是否已存在
        WxUser existingUser = findByPhone(newUser.getPhone());
        if (existingUser != null) {
            return false; // 已存在，注册失败
        }
        // 对密码进行加密处理
        newUser.setPassword(DigestUtils.md5DigestAsHex(newUser.getPassword().getBytes()));
        newUser.setCreateTime(new Date());
        return this.save(newUser);
    }

    @Override
    public boolean disableUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return true;
        }
        List<WxUser> usersToUpdate = userIds.stream()
                .map(id -> {
                    WxUser user = new WxUser();
                    user.setUserId(id.intValue());
                    user.setStatus(false); // 禁用
                    return user;
                }).collect(Collectors.toList());
        return this.updateBatchById(usersToUpdate);
    }

    @Override
    public boolean enableUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return true;
        }
        List<WxUser> usersToUpdate = userIds.stream()
                .map(id -> {
                    WxUser user = new WxUser();
                    user.setUserId(id.intValue());
                    user.setStatus(true); // 启用
                    return user;
                }).collect(Collectors.toList());
        return this.updateBatchById(usersToUpdate);
    }
}
