package com.itmk.netSystem.LLM.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.LLM.entity.LLMstore;

public interface LLMService extends IService<LLMstore> {
    String getLoads(String message);
    String getCommonQuestionLoads(Long id);
}
