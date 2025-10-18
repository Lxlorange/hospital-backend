package com.itmk.netSystem.see.service.implement;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.itmk.netSystem.call.entity.MakeOrder;
import com.itmk.netSystem.see.entity.MakeOrderVisit;
import com.itmk.netSystem.see.entity.SeePage;
import com.itmk.netSystem.see.mapper.SeeMapper;
import com.itmk.netSystem.see.service.SeeService;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.treatpatient.entity.VisitUser;
import com.itmk.netSystem.userWeb.entity.SysUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
public class SeeServiceImpl extends ServiceImpl<SeeMapper, MakeOrderVisit> implements SeeService {
    @Override
    public boolean addVisit(MakeOrderVisit makeOrderVisit) {
        makeOrderVisit.setCreateTime(new Date());
        makeOrderVisit.setHasVisit("0"); // 默认未就诊
        return save(makeOrderVisit);
    }

    @Override
    @Transactional
    public boolean deleteVisitAndResetOrder(Integer visitId) {
        MakeOrderVisit visit = getById(visitId);
        if (visit != null) {
            // 恢复预约表状态
            MakeOrder makeOrder = new MakeOrder();
            makeOrder.setMakeId(visit.getMakeId());
            makeOrder.setHasVisit("0");
            return removeById(visitId);
        }
        return false;
    }

    @Override
    public MakeOrderVisit getVisitDetails(Integer visitId) {
        MPJLambdaWrapper<MakeOrderVisit> query = new MPJLambdaWrapper<>();
        query.selectAll(MakeOrderVisit.class)
                .select(VisitUser::getVisitname)
                .select(Department::getDeptName)
                .select(SysUser::getNickName)
                .leftJoin(SysUser.class,SysUser::getUserId,MakeOrderVisit::getDoctorId)
                .leftJoin(VisitUser.class,VisitUser::getVisitId,MakeOrderVisit::getVisitUserId)
                .leftJoin(Department.class,Department::getDeptId,SysUser::getDeptId)
                .eq(MakeOrderVisit::getVisitId, visitId);
        return getBaseMapper().selectOne(query);
    }


    public IPage<MakeOrderVisit> getVisitsByDateRange(SeePage params) {
        IPage<MakeOrderVisit> page = new Page<>(params.getCurrentPage(), params.getPageSize());
        MPJLambdaWrapper<MakeOrderVisit> query = new MPJLambdaWrapper<>();
        return page(page, query);
    }

    @Override
    public Integer getVisitCountByDoctor(Integer doctorId) {
        MPJLambdaWrapper<MakeOrderVisit> query = new MPJLambdaWrapper<>();
        query.eq(MakeOrderVisit::getDoctorId, doctorId);
        return doctorId;
    }
}
