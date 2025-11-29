package com.itmk.netSystem.captcha;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.anji.captcha.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    // Starter 已经自动把 CaptchaService 注入到容器里了，这里直接用
    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/get")
    public ResponseModel get(@RequestBody CaptchaVO data, HttpServletRequest request) {
        // 建议加上断言
        assert request.getRemoteHost() != null;
        data.setBrowserInfo(getRemoteId(request));
        return captchaService.get(data);
    }

    @PostMapping("/check")
    public ResponseModel check(@RequestBody CaptchaVO data, HttpServletRequest request) {
        data.setBrowserInfo(getRemoteId(request));
        return captchaService.check(data);
    }

    // 辅助方法：获取客户端IP
    public static String getRemoteId(HttpServletRequest request) {
        String xfwd = request.getHeader("X-Forwarded-For");
        String ip = getRemoteIpFromXfwd(xfwd);
        String ua = request.getHeader("user-agent");
        if (StringUtils.isNotBlank(ip)) {
            return ip + ua;
        }
        return request.getRemoteAddr() + ua;
    }

    private static String getRemoteIpFromXfwd(String xfwd) {
        if (StringUtils.isNotBlank(xfwd)) {
            String[] ipList = xfwd.split(",");
            return ipList[0].trim();
        }
        return null;
    }
}