package com.itmk.netSystem.loginLog.service.implement;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.loginLog.entity.LoginLog;
import com.itmk.netSystem.loginLog.mapper.LoginLogMapper;
import com.itmk.netSystem.loginLog.service.LoginLogService;
import org.springframework.stereotype.Service;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {
}