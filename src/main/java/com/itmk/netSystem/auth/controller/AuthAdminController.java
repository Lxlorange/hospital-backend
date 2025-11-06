package com.itmk.netSystem.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itmk.netSystem.auth.entity.IdentityAuthRequest;
import com.itmk.netSystem.auth.service.IdentityAuthService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台身份认证接口
 */
@RestController
@RequestMapping("/api/auth")
public class AuthAdminController {

    @Autowired
    private IdentityAuthService identityAuthService;

    /**
     * 1. 获取验证身份申请
     * GET /api/auth/list
     * 返回格式：{ records: [...] }
     */
    @GetMapping("/list")
    public ResultVo list() {
        QueryWrapper<IdentityAuthRequest> query = new QueryWrapper<>();
        query.lambda().orderByDesc(IdentityAuthRequest::getCreateTime);
        List<IdentityAuthRequest> list = identityAuthService.list(query);
        Map<String, Object> data = new HashMap<>();
        data.put("records", list);
        return ResultUtils.success("success", data);
    }

    /**
     * 2. 批准身份认证
     * POST /api/auth/approve
     */
    @PostMapping("/approve")
    public ResultVo approve(@RequestBody Map<String, Object> body) {
        Object reqIdObj = body.get("requestId");
        String reviewComment = (String) body.get("reviewComment");
        if (reqIdObj == null) {
            return ResultUtils.error("参数错误");
        }
        Long requestId = Long.valueOf(reqIdObj.toString());
        // 审核人ID可选，后续可接入后台用户体系
        Long reviewerId = null;
        boolean ok = identityAuthService.approve(requestId, reviewComment, reviewerId);
        if (!ok) {
            return ResultUtils.error("审核失败");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("requestId", requestId);
        data.put("reviewComment", reviewComment);
        return ResultUtils.success("success", data);
    }

    /**
     * 3. 驳回身份认证
     * POST /api/auth/reject
     */
    @PostMapping("/reject")
    public ResultVo reject(@RequestBody Map<String, Object> body) {
        Object reqIdObj = body.get("requestId");
        String reviewComment = (String) body.get("reviewComment");
        if (reqIdObj == null) {
            return ResultUtils.error("参数错误");
        }
        Long requestId = Long.valueOf(reqIdObj.toString());
        Long reviewerId = null;
        boolean ok = identityAuthService.reject(requestId, reviewComment, reviewerId);
        if (!ok) {
            return ResultUtils.error("审核失败");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("requestId", requestId);
        data.put("reviewComment", reviewComment);
        return ResultUtils.success("success", data);
    }
}