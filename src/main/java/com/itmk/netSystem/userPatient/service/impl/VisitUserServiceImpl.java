package com.itmk.netSystem.userPatient.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.userPatient.entity.VisitUser;
import com.itmk.netSystem.userPatient.mapper.VisitUserMapper;
import com.itmk.netSystem.userPatient.service.VisitUserService;
import org.springframework.stereotype.Service;

 
@Service
public class VisitUserServiceImpl extends ServiceImpl<VisitUserMapper, VisitUser> implements VisitUserService {
}
