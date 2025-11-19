package com.itmk.netSystem.LLM.service;

import com.itmk.netSystem.LLM.entity.LLMstore;
import com.itmk.netSystem.LLM.mapper.LLMMapper;
import com.itmk.netSystem.LLM.util.ChatPayloadBuilder;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LLMServiceImpl extends ServiceImpl<LLMMapper, LLMstore> implements LLMService {
    @Value("${llm.siliconflow.apiKey:}")
    private String apiKey;

    @Value("${llm.siliconflow.model:Qwen/Qwen3-30B-A3B-Thinking-2507}")
    private String model;

    @Value("${llm.defaultSystemPrompt:你是医院后台的智能助手，只回答与本系统功能和使用相关的问题；拒绝提供医疗建议或与系统无关的信息。回答准确、简洁，面向用户实际操作场景。}")
    private String defaultSystemPrompt;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getLoads(String message) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("未配置 LLM API Key");
        }
        String systemPrompt = defaultSystemPrompt;
        String finalMessage = message;


        ChatPayloadBuilder builder = new ChatPayloadBuilder(model, systemPrompt, finalMessage, 0.3);
        String payload = builder.build();

        HttpResponse<String> response = Unirest.post("https://api.siliconflow.cn/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(payload)
                .asString();

        String body = response.getBody();
        return body;
    }
    @Override
    public String getCommonQuestionLoads(Long id) throws IllegalArgumentException {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("未配置 LLM API Key");
        }
        String systemPrompt = defaultSystemPrompt;
        String finalMessage = "";
        if (id != null) {
            LLMstore store = this.getById(id);
            if (store != null) {
                if (store.getPrompt() != null && !store.getPrompt().trim().isEmpty()) {
                    systemPrompt = store.getPrompt();
                }
                if (store.getMessage() != null && !store.getMessage().trim().isEmpty()) {
                    finalMessage = store.getMessage();
                }
            }
            else{
                throw new IllegalArgumentException("没有找到对应问题！");
            }
        }
        ChatPayloadBuilder builder = new ChatPayloadBuilder(model, systemPrompt, finalMessage, 0.3);
        String payload = builder.build();

        HttpResponse<String> response = Unirest.post("https://api.siliconflow.cn/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(payload)
                .asString();
        return response.getBody();
    }

    private String escape(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
