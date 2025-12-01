package com.itmk.netSystem.journal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.netSystem.journal.entity.News;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
 
public interface JournalMapper extends BaseMapper<News> {
    List<News> findIndexNews();


    int deleteBatchByIds(@Param("ids") List<Long> ids);


    List<News> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);


    List<News> findLatestNews(@Param("limit") int limit);


    int archiveNewsById(@Param("id") Long id);
}
