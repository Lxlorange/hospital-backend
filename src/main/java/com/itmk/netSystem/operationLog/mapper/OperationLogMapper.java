package com.itmk.netSystem.operationLog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.netSystem.operationLog.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}