package com.itmk.netSystem.userPatientPhone.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.userPatientPhone.entity.WxUser;

import java.util.List;


public interface UserPatientPhoneService extends IService<WxUser> {
    /**
     * 根据手机号码查找用户。
     * @param phone 手机号码
     * @return 用户实体，如果找不到则返回null。
     */
    WxUser findByPhone(String phone);

    /**
     * 根据用户名查找用户。
     * @param username 用户名
     * @return 用户实体。
     */
    WxUser findByUsername(String username);

    /**
     * 根据邮箱查找用户。
     * @param email 用户邮箱
     * @return 匹配的用户对象，如果不存在则返回null。
     */
    WxUser findByEmail(String email);

    /**
     * 注册一个新用户，会检查手机号是否已存在。
     * @param newUser 新用户的实体对象
     * @return 注册成功返回true，如果手机号已存在则返回false。
     */
    boolean register(WxUser newUser);

    /**
     * 批量禁用用户账户。
     * @param userIds 需要禁用的用户ID列表
     * @return 操作成功返回true。
     */
    boolean disableUsers(List<Long> userIds);

    /**
     * 批量启用用户账户。
     * @param userIds 需要启用的用户ID列表
     * @return 操作成功返回true。
     */
    boolean enableUsers(List<Long> userIds);
}
