package com.itmk.config.service;

/**
 * ProjectName: backend
 * ClassName: GeetestService
 * Package : com.itmk.config.service
 * Description:
 *
 * @Author Lxl
 * @Create 2025/10/30 17:06
 * @Version 1.0
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmk.netSystem.call.entity.MakeOrder;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class GeetestService {

    @Value("${geetest.enabled:true}") // 默认为 true，保证生产安全
    private boolean geetestEnabled;

    @Value("${geetest.id}")
    private String geetestId;

    @Value("${geetest.key}")
    private String geetestKey;

    @Value("${geetest.api.url}")
    private String geetestApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 验证 Geetest V4 结果 - 手动 API 版
     * @param request 包含 Geetest 参数的请求
     * @param userId 用户的唯一标识
     * @return 验证结果
     */
    public boolean verify(MakeOrder request, String userId) {
        if (!geetestEnabled) {
            return true;
        }
        // (注意) 这里我们引用的是 MakeOrder.GeetestParam
        MakeOrder.GeetestParam geetestParam = request.getGeetest();
        if (geetestParam == null || geetestParam.getLot_number() == null) {
            return false; // 缺少验证参数
        }

        try {
            // --- 步骤 2: 获取客户端参数并签名 ---
            String lotNumber = geetestParam.getLot_number();

            // 使用 HmacSHA256(lot_number, captcha_key) 生成 sign_token
            HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, geetestKey.getBytes(StandardCharsets.UTF_8));
            String signToken = hmacUtils.hmacHex(lotNumber.getBytes(StandardCharsets.UTF_8));

            // --- 步骤 3: 上传验证参数提交二次校验 ---
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("captcha_id", geetestId);
            params.add("lot_number", lotNumber);
            params.add("pass_token", geetestParam.getPass_token());
            params.add("gen_time", geetestParam.getGen_time());
            params.add("captcha_output", geetestParam.getCaptcha_output());
            params.add("sign_token", signToken);
            params.add("user_id", userId);

            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(geetestApiUrl, httpEntity, String.class);

            // --- 步骤 4: 处理容灾降级逻辑 (部分) ---
            if (response.getStatusCode() != HttpStatus.OK) {
                System.err.println("Geetest 验证: 响应状态非 200，执行容灾放行");
                return true;
            }

            // --- 步骤 5: 处理业务逻辑 ---
            String body = response.getBody();
            if (body == null) {
                System.err.println("Geetest 验证: 响应体为空，执行容灾放行");
                return true; // 异常情况，放行
            }

            Map<String, String> responseMap = objectMapper.readValue(body, Map.class);

            // "仅当二次校验接口返回 result 为 success 时，才允许通过业务流程"
            return "success".equals(responseMap.get("result"));

        } catch (Exception e) {
            // --- 步骤 4: 处理容灾降级逻辑 (异常) ---
            System.err.println("Geetest 验证异常: " + e.getMessage() + "，执行容灾放行");
            return true; // (关键：容灾降级，放行)
        }
    }
}