package com.itmk.netSystem.journal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.netSystem.journal.entity.JournalPage;
import com.itmk.netSystem.journal.entity.News;
import com.itmk.netSystem.journal.service.JournalService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
 
@RequestMapping("/api/news")
@RestController
public class JournalController {
    @Autowired
    private JournalService journalService;
    @GetMapping("/getList")
    public ResultVo getList(JournalPage parm){
        IPage<News> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        QueryWrapper<News> query = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(parm.getKeywords())){
            query.lambda().like(News::getTitle,parm.getKeywords())
                    .or()
                    .like(News::getTextDesc,parm.getKeywords())
                    .or()
                    .like(News::getTextContent,parm.getKeywords());
        }
        query.lambda().orderByDesc(News::getCreateTime);
        IPage<News> list = journalService.page(page, query);
        return ResultUtils.success("成功!",list);
    }


    @GetMapping("/{id}")
    public ResultVo getJournalById(@PathVariable("id") Long id) {
        News news = journalService.getById(id);
        if (news != null) {
            return ResultUtils.success("查询成功!", news);
        }
        return ResultUtils.error("查询失败, 新闻不存在!");
    }

    @PreAuthorize("hasAuthority('sys:news:publish')")
    @PostMapping("/publish/{id}")
    public ResultVo publishJournal(@PathVariable("id") Long id) {
        News news = journalService.getById(id);
        if (news != null) {
            news.setToIndex("1");
            if(journalService.updateById(news)){
                return ResultUtils.success("新闻发布成功!");
            }
        }
        return ResultUtils.error("新闻发布失败!");
    }

    @PutMapping("edit")
    @PreAuthorize("hasAuthority('sys:news:edit')")
    public ResultVo edit(@RequestBody News news){
        if(journalService.updateById(news)){
            return ResultUtils.success("成功!");
        }
        return ResultUtils.error("失败!");
    }

    @PostMapping("add")
    @PreAuthorize("hasAuthority('sys:news:add')")
    public ResultVo add(@RequestBody News news){
        news.setCreateTime(new Date());
        if(journalService.save(news)){
            return ResultUtils.success("成功!");
        }
        return ResultUtils.error("失败!");
    }

    @PreAuthorize("hasAuthority('sys:news:delete')")
    @DeleteMapping("/batch")
    public ResultVo deleteBatch(@RequestBody List<Long> ids) {
        if (journalService.removeByIds(ids)) {
            return ResultUtils.success("批量删除成功!");
        }
        return ResultUtils.error("批量删除失败!");
    }

    @GetMapping("/index")
    public ResultVo getIndexJournal() {
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda().eq(News::getToIndex, "1").orderByDesc(News::getCreateTime);
        List<News> list = journalService.list(query);
        return ResultUtils.success("查询首页新闻成功!", list);
    }

    @GetMapping("/latest/{count}")
    public ResultVo getLatestJournal(@PathVariable("count") int count) {
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda().orderByDesc(News::getCreateTime).last("LIMIT " + count);
        List<News> list = journalService.list(query);
        return ResultUtils.success("查询最新新闻成功!", list);
    }

    @PreAuthorize("hasAuthority('sys:news:delete')")
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id){
        if(journalService.removeById(id)){
            return ResultUtils.success("成功!");
        }
        return ResultUtils.error("失败!");
    }
}
