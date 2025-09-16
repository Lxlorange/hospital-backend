package com.itmk.web.visit_user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.visit_user.entity.VisitUser;
import com.itmk.web.visit_user.mapper.VisitUserMapper;
import com.itmk.web.visit_user.service.VisitUserService;
import org.springframework.stereotype.Service;

 
@Service
public class VisitUserServiceImpl extends ServiceImpl<VisitUserMapper, VisitUser> implements VisitUserService {
}
