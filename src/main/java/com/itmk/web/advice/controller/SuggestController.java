package com.itmk.web.advice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.advice.entity.Suggest;
import com.itmk.web.advice.entity.SuggestPageParm;
import com.itmk.web.advice.service.SuggestService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

 
@RestController
@RequestMapping("/api/suggest")
public class SuggestController {
    @Autowired
    private SuggestService suggestService;

    /**
     * 列表查询
     * @param parm
     * @return
     */
    @GetMapping("/getList")
    public ResultVo getList(SuggestPageParm parm){
        //构造分页对象
        IPage<Suggest> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        //构造查询条件
        QueryWrapper<Suggest> query = new QueryWrapper<>();
        query.lambda()
                .like(StringUtils.isNotEmpty(parm.getName()),Suggest::getTitle,parm.getName())
                .or()
                .like(StringUtils.isNotEmpty(parm.getName()),Suggest::getContent,parm.getName())
                .orderByDesc(Suggest::getCreateTime);
        IPage<Suggest> list = suggestService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 删除
     * @param sguuestId
     * @return
     */
    @DeleteMapping("/{sguuestId}")
    public ResultVo delete(@PathVariable("sguuestId") Integer sguuestId){
        if(suggestService.removeById(sguuestId)){
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败!");
    }
}
