package com.itmk.netSystem.userPatientPhone.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.netSystem.userPatientPhone.entity.WxUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserPatientPhoneMapper extends BaseMapper<WxUser> {
    /**
     * 根据手机号码查询唯一用户。
     * @param phone 手机号码
     * @return 匹配的用户对象，如果不存在则返回null。
     */
    WxUser selectByPhone(@Param("phone") String phone);

    /**
     * 根据用户名查询唯一用户。
     * @param userName 用户名
     * @return 匹配的用户对象。
     */
    WxUser selectByUserName(@Param("userName") String userName);

    /**
     * 批量更新用户的状态（启用/禁用）。
     * @param userIds 用户ID列表
     * @param status 目标状态
     * @return 成功更新的记录数。
     */
    int updateStatusForBatch(@Param("userIds") List<Long> userIds, @Param("status") boolean status);

    /**
     * 查询所有状态为激活的用户。
     * @return 所有激活用户的列表。
     */
    List<WxUser> selectAllActiveUsers();

    /**
     * 统计指定状态的用户总数。
     * @param status 用户状态
     * @return 匹配的用户总数。
     */
    Integer countUsersByStatus(@Param("status") boolean status);
}
