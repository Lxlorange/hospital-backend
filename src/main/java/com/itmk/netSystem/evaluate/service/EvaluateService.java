package com.itmk.netSystem.evaluate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.evaluate.entity.Suggest;

 
public interface EvaluateService extends IService<Suggest> {
    /**
     * 分页查询列表，并关联查询用户信息
     * @param page 分页对象
     * @param name 搜索关键字 (对应 title 或 content)
     * @return
     */
    com.baomidou.mybatisplus.core.metadata.IPage<Suggest> getListWithUser(com.baomidou.mybatisplus.core.metadata.IPage<Suggest> page, String name);

    /**
     * 根据ID查询详情，并关联查询用户信息
     * @param suggestId
     * @return
     */
    Suggest getByIdWithUser(Integer suggestId);

    /**
     * 根据用户ID查询所有反馈
     * @param userId
     * @return
     */
    java.util.List<Suggest> getListByUserId(Integer userId);
}
