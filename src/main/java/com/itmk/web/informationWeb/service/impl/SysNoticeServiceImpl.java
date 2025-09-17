package com.itmk.web.informationWeb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.informationWeb.entity.SysNotice;
import com.itmk.web.informationWeb.mapper.SysNoticeMapper;
import com.itmk.web.informationWeb.service.SysNoticeService;
import org.springframework.stereotype.Service;

 
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {
}
