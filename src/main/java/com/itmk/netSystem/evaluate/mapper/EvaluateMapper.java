package com.itmk.netSystem.evaluate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.netSystem.evaluate.entity.Suggest;

 
public interface EvaluateMapper extends BaseMapper<Suggest> {
    /**
     * @param page
     * @param name
     * @return
     */
    @org.apache.ibatis.annotations.Select("SELECT s.*, u.nick_name, u.image " +
            "FROM suggest s " +
            "LEFT JOIN sys_user u ON s.user_id = u.user_id " +
            "WHERE s.title LIKE CONCAT('%', #{name}, '%') OR s.content LIKE CONCAT('%', #{name}, '%') " +
            "ORDER BY s.create_time DESC")
    com.baomidou.mybatisplus.core.metadata.IPage<Suggest> getListWithUser(com.baomidou.mybatisplus.core.metadata.IPage<Suggest> page, @org.apache.ibatis.annotations.Param("name") String name);

    /**
     * 根据ID查询详情，并关联查询用户信息 (假设用户表为 sys_user)
     * @param suggestId
     * @return
     */
    @org.apache.ibatis.annotations.Select("SELECT s.*, u.nick_name, u.image " +
            "FROM suggest s " +
            "LEFT JOIN sys_user u ON s.user_id = u.user_id " +
            "WHERE s.id = #{suggestId}")
    Suggest getByIdWithUser(@org.apache.ibatis.annotations.Param("suggestId") Integer suggestId);

    /**
     * 查询最新的反馈
     * @return
     */
    @org.apache.ibatis.annotations.Select("SELECT * FROM suggest ORDER BY create_time DESC LIMIT 1")
    Suggest findLatestSuggestion();
}
