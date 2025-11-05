package com.itmk.netSystem.evaluate.service.implement;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.evaluate.entity.Suggest;
import com.itmk.netSystem.evaluate.mapper.EvaluateMapper;
import com.itmk.netSystem.evaluate.service.EvaluateService;
import org.springframework.stereotype.Service;

 
@Service
public class EvaluateServiceImplement extends ServiceImpl<EvaluateMapper, Suggest> implements EvaluateService {
    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<Suggest> getListWithUser(com.baomidou.mybatisplus.core.metadata.IPage<Suggest> page, String name) {
        return baseMapper.getListWithUser(page, name);
    }

    @Override
    public Suggest getByIdWithUser(Integer suggestId) {
        // 此方法需要 EvaluateMapper.java 中有对应的 getByIdWithUser 方法
        return baseMapper.getByIdWithUser(suggestId);
    }

    @Override
    public java.util.List<Suggest> getListByUserId(Integer userId) {
        // 这种简单查询可以直接使用 ServiceImpl 提供的 QueryWrapper
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Suggest> query = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        query.lambda().eq(Suggest::getUserId, userId)
                .orderByDesc(Suggest::getCreateTime);
        return this.list(query);
    }
}
