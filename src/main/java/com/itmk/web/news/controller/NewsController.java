package com.itmk.web.news.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.department.entity.DepartmentPageParm;
import com.itmk.web.news.entity.News;
import com.itmk.web.news.entity.NewsPageParm;
import com.itmk.web.news.service.NewsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

 
@RequestMapping("/api/news")
@RestController
public class NewsController {
    @Autowired
    private NewsService newsService;
    /**
     * 列表查询
     * @param parm
     * @return
     */
    @GetMapping("/getList")
    public ResultVo getList(NewsPageParm parm){
        //构造分页对象
        IPage<News> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        //构造查询条件
        QueryWrapper<News> query = new QueryWrapper<>();
        //模糊查询 like
        if(StringUtils.isNotEmpty(parm.getKeywords())){
            //like查询
            query.lambda().like(News::getTitle,parm.getKeywords())
                    .or()
                    .like(News::getTextDesc,parm.getKeywords())
                    .or()
                    .like(News::getTextContent,parm.getKeywords());
        }
        //排序：order_num字段
        query.lambda().orderByDesc(News::getCreateTime);
        IPage<News> list = newsService.page(page, query);
        return ResultUtils.success("查询成功!",list);
    }

    /**
     * 新增
     * @param news
     * @return
     */
    @PostMapping("add")
    @PreAuthorize("hasAuthority('sys:news:add')")
    public ResultVo add(@RequestBody News news){
        news.setCreateTime(new Date());
        if(newsService.save(news)){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }

    /**
     * 编辑
     * @param news
     * @return
     */
    @PutMapping("edit")
    @PreAuthorize("hasAuthority('sys:news:edit')")
    public ResultVo edit(@RequestBody News news){
        if(newsService.updateById(news)){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    //删除
    @PreAuthorize("hasAuthority('sys:news:delete')")
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id){
        if(newsService.removeById(id)){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }
}
