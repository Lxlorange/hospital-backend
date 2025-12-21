package com.itmk.netSystem.loginLog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.netSystem.loginLog.entity.LoginLog;
import com.itmk.netSystem.loginLog.entity.LoginLogQuery;
import com.itmk.netSystem.loginLog.service.LoginLogService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loginLog")
public class LoginLogController {
    @Autowired
    private LoginLogService loginLogService;

    @GetMapping("/list")
    public ResultVo list(LoginLogQuery parm) {
        LoginLogQuery q = parm != null ? parm : new LoginLogQuery();
        long currentPage = (q.getCurrentPage() == null || q.getCurrentPage() <= 0) ? 1L : q.getCurrentPage();
        long pageSize = (q.getPageSize() == null || q.getPageSize() <= 0) ? 10L : q.getPageSize();

        IPage<LoginLog> page = new Page<>(currentPage, pageSize);
        QueryWrapper<LoginLog> query = new QueryWrapper<>();
        query.lambda()
                .like(StringUtils.isNotEmpty(q.getName()), LoginLog::getNickName, q.getName())
                .orderByDesc(LoginLog::getLoginTime);
        IPage<LoginLog> list = loginLogService.page(page, query);
        return ResultUtils.success("成功", list);
    }
}