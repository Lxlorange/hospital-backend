package com.itmk.netSystem.treatpatient.service.implement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.treatpatient.entity.VisitUser;
import com.itmk.netSystem.treatpatient.mapper.TreatPatientMapper;
import com.itmk.netSystem.treatpatient.service.TreatPatientService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TreatPatientServiceImplement extends ServiceImpl<TreatPatientMapper, VisitUser> implements TreatPatientService {
    @Override
    public boolean registerNewPatient(VisitUser visitUser) {
        // 检查身份证号是否已经存在
        QueryWrapper<VisitUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id_card", visitUser.getIdCard());
        if (this.baseMapper.selectCount(queryWrapper) > 0) {
            // 如果已存在，则注册失败
            return false;
        }
        // 不存在则保存
        return this.save(visitUser);
    }

    @Override
    public VisitUser findPatientByIdCard(String idCard) {
        QueryWrapper<VisitUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id_card", idCard);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<VisitUser> searchPatientsByName(String name) {
        QueryWrapper<VisitUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("visitname", name);
        return this.list(queryWrapper);
    }

    @Override
    public boolean deletePatientsInBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return this.removeByIds(ids);
    }

    @Override
    public boolean updatePatientByPhone(VisitUser visitUser) {
        if (visitUser.getPhone() == null) {
            return false;
        }
        QueryWrapper<VisitUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", visitUser.getPhone());
        return this.update(visitUser, queryWrapper);
    }
}
