package com.itmk.web.userPhone.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.userPhone.entity.WxUser;
import com.itmk.web.userPhone.mapper.WxUserMapper;
import com.itmk.web.userPhone.service.WxUserService;
import org.springframework.stereotype.Service;

 
@Service
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements WxUserService {
}
