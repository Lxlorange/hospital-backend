package com.itmk.netSystem.see.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.see.entity.MakeOrderVisit;
import com.itmk.netSystem.see.entity.SeePage;


public interface SeeService extends IService<MakeOrderVisit> {
    /**
     * 新增一个就诊记录
     * @param makeOrderVisit
     * @return
     */
    boolean addVisit(MakeOrderVisit makeOrderVisit);

    /**
     * 删除就诊记录并重置对应的预约单状态
     * @param visitId
     * @return
     */
    boolean deleteVisitAndResetOrder(Integer visitId);

    /**
     * 获取包含关联信息的详细就诊视图
     * @param visitId
     * @return
     */
    MakeOrderVisit getVisitDetails(Integer visitId);



    /**
     * 统计某位医生的接诊总数
     * @param doctorId
     * @return
     */
    Integer getVisitCountByDoctor(Integer doctorId);
}
