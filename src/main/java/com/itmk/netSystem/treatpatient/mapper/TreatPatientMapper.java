package com.itmk.netSystem.treatpatient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.netSystem.treatpatient.entity.VisitUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface TreatPatientMapper extends BaseMapper<VisitUser> {
    /**
     * 根据身份证号精确查找就诊人信息。
     * @param idCard 身份证号码
     * @return 返回匹配的就诊人对象，如果没有找到则返回null。
     */
    VisitUser findByIdCard(@Param("idCard") String idCard);

    /**
     * 根据姓名模糊查询就诊人列表。
     * @param name 姓名关键字
     * @return 返回包含该关键字的所有就诊人列表。
     */
    List<VisitUser> findByNameContaining(@Param("name") String name);

    /**
     * 统计指定性别的就诊人数量。
     * @param sex 性别代码 ("0" for 女, "1" for 男)
     * @return 返回该性别的总人数。
     */
    Integer countBySex(@Param("sex") String sex);

    /**
     * 根据电话号码查找就诊人。
     * @param phone 电话号码
     * @return 返回匹配的就诊人对象。
     */
    VisitUser findByPhone(@Param("phone") String phone);

    /**
     * 批量删除就诊人。
     * @param ids 就诊人ID列表
     * @return 返回成功删除的记录数。
     */
    int deleteBatchByIds(@Param("ids") List<Integer> ids);
}
