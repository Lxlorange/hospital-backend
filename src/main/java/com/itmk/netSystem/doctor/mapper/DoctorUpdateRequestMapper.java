package com.itmk.netSystem.doctor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.netSystem.doctor.entity.DoctorUpdateRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生信息更新申请表Mapper接口
 */
@Mapper
public interface DoctorUpdateRequestMapper extends BaseMapper<DoctorUpdateRequest> {
}