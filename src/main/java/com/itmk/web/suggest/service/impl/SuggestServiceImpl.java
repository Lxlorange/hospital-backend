package com.itmk.web.suggest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.suggest.entity.Suggest;
import com.itmk.web.suggest.mapper.SuggestMapper;
import com.itmk.web.suggest.service.SuggestService;
import org.springframework.stereotype.Service;

 
@Service
public class SuggestServiceImpl extends ServiceImpl<SuggestMapper, Suggest> implements SuggestService {
}
