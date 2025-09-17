package com.itmk.web.userPatient.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.userPatient.entity.VisitUser;
import com.itmk.web.userPatient.mapper.VisitUserMapper;
import com.itmk.web.userPatient.service.VisitUserService;
import org.springframework.stereotype.Service;

 
@Service
public class VisitUserServiceImpl extends ServiceImpl<VisitUserMapper, VisitUser> implements VisitUserService {
}
