package com.itmk.netSystem.announceWeb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.netSystem.announceWeb.entity.SysNotice;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
 
public interface AnnounceWebMapper extends BaseMapper<SysNotice> {

    int deleteBatchIds(@Param("ids") List<Long> ids);


    List<SysNotice> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);


    List<SysNotice> findLatestNotices(@Param("limit") int limit);


    int incrementViewCount(@Param("noticeId") Long noticeId);


    List<SysNotice> findByKeyword(@Param("keyword") String keyword);
}
