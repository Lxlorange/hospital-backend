package com.itmk.netSystem.journal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.journal.entity.News;

import java.util.List;
 
public interface JournalService extends IService<News> {
    News getNewsById(Long id);


    List<News> getIndexNews();


    boolean deleteNewsInBatch(List<Long> ids);


    boolean publishNews(Long id);


    List<News> getLatestNews(int count);
}
