package com.itmk.netSystem.journal.service.implement;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.journal.entity.News;
import com.itmk.netSystem.journal.mapper.JournalMapper;
import com.itmk.netSystem.journal.service.JournalService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Service
public class JournalImplement extends ServiceImpl<JournalMapper, News> implements JournalService {
    @Override
    public News getNewsById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public List<News> getIndexNews() {
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda().eq(News::getToIndex, "1").orderByDesc(News::getCreateTime);
        return this.baseMapper.selectList(query);
    }

    @Override
    public boolean deleteNewsInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        int affectedRows = this.baseMapper.deleteBatchIds(ids);
        return affectedRows > 0;
    }

    @Override
    public boolean publishNews(Long id) {
        News news = new News();
        news.setId(id.intValue());
        news.setToIndex("1"); // Assuming '1' means published to index
        return this.updateById(news);
    }

    @Override
    public List<News> getLatestNews(int count) {
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda().orderByDesc(News::getCreateTime).last("LIMIT " + count);
        return this.baseMapper.selectList(query);
    }
}
