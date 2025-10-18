package com.itmk.netSystem.treatpatient.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.treatpatient.entity.VisitUser;

import java.util.List;


public interface TreatPatientService extends IService<VisitUser> {
    /**
     * 注册一个新的就诊人，并检查身份证号是否已存在。
     * @param visitUser 要注册的就诊人信息
     * @return 如果注册成功返回true，如果身份证号已存在则返回false。
     */
    boolean registerNewPatient(VisitUser visitUser);

    /**
     * 根据身份证号查找唯一的就诊人。
     * @param idCard 身份证号码
     * @return 返回找到的就诊人信息。
     */
    VisitUser findPatientByIdCard(String idCard);

    /**
     * 根据姓名关键字搜索就诊人列表。
     * @param name 姓名关键字
     * @return 返回匹配的就诊人列表。
     */
    List<VisitUser> searchPatientsByName(String name);

    /**
     * 批量删除就诊人记录。
     * @param ids 要删除的就诊人ID列表
     * @return 如果操作成功返回true。
     */
    boolean deletePatientsInBatch(List<Integer> ids);

    /**
     * 根据电话号码更新就诊人信息。
     * @param visitUser 包含新信息的就诊人对象，其中电话号码用于查询
     * @return 如果更新成功返回true。
     */
    boolean updatePatientByPhone(VisitUser visitUser);
}
