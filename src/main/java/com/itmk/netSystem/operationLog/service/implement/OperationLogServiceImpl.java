package com.itmk.netSystem.operationLog.service.implement;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.operationLog.entity.OperationLog;
import com.itmk.netSystem.operationLog.mapper.OperationLogMapper;
import com.itmk.netSystem.operationLog.service.OperationLogService;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
}