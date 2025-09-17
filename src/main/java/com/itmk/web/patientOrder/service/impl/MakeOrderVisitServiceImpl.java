package com.itmk.web.patientOrder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.patientOrder.entity.MakeOrderVisit;
import com.itmk.web.patientOrder.mapper.MakeOrderVisitMapper;
import com.itmk.web.patientOrder.service.MakeOrderVisitService;
import org.springframework.stereotype.Service;

 
@Service
public class MakeOrderVisitServiceImpl extends ServiceImpl<MakeOrderVisitMapper, MakeOrderVisit> implements MakeOrderVisitService {
}
