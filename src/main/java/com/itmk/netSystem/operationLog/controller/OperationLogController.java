package com.itmk.netSystem.operationLog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.netSystem.operationLog.entity.OperationLog;
import com.itmk.netSystem.operationLog.service.OperationLogService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operationLog")
public class OperationLogController {
    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/list")
    public ResultVo list(@RequestParam(value = "currentPage", required = false) Long currentPage,
                         @RequestParam(value = "pageSize", required = false) Long pageSize,
                         @RequestParam(value = "name", required = false) String name) {
        long cp = (currentPage == null || currentPage <= 0) ? 1L : currentPage;
        long ps = (pageSize == null || pageSize <= 0) ? 10L : pageSize;
        IPage<OperationLog> page = new Page<>(cp, ps);
        QueryWrapper<OperationLog> query = new QueryWrapper<>();
        query.lambda()
                .like(StringUtils.isNotEmpty(name), OperationLog::getNickName, name)
                .orderByDesc(OperationLog::getOperateTime);
        IPage<OperationLog> list = operationLogService.page(page, query);
        return ResultUtils.success("成功", list);
    }

    @org.springframework.web.bind.annotation.PostMapping("/openMenu")
    public ResultVo openMenu(jakarta.servlet.http.HttpServletRequest request,
                             @org.springframework.web.bind.annotation.RequestBody java.util.Map<String, Object> body) {
        String menuName = body != null && body.get("menuName") != null ? body.get("menuName").toString() : null;
        String path = body != null && body.get("path") != null ? body.get("path").toString() : null;
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.itmk.netSystem.userWeb.entity.SysUser) {
            com.itmk.netSystem.userWeb.entity.SysUser user = (com.itmk.netSystem.userWeb.entity.SysUser) authentication.getPrincipal();
            OperationLog log = new OperationLog();
            log.setUserId(user.getUserId());
            log.setUsername(user.getUsername());
            log.setNickName(user.getNickName());
            String ip = request.getHeader("X-Forwarded-For");
            if (org.apache.commons.lang3.StringUtils.isEmpty(ip)) {
                ip = request.getRemoteAddr();
            }
            log.setIpAddr(ip);
            log.setOperation(menuName != null ? ("打开菜单 " + menuName) : ("打开菜单"));
            log.setMethod("MENU");
            log.setUri(path);
            log.setStatus(200);
            log.setOperateTime(new java.util.Date());
            operationLogService.save(log);
        }
        return ResultUtils.success("记录成功");
    }
}