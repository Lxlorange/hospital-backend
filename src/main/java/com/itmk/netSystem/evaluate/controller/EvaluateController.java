package com.itmk.netSystem.evaluate.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.netSystem.evaluate.entity.Suggest;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.netSystem.evaluate.entity.Evaluate;
import com.itmk.netSystem.evaluate.service.EvaluateService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

 
@RestController
@RequestMapping("/api/suggest")
public class EvaluateController {
    @Autowired
    private EvaluateService evaluateService;


    /**
     * 新增
     * @param suggest
     * @return
     */
    @PostMapping
    public ResultVo add(@RequestBody Suggest suggest){
        suggest.setCreateTime(new java.util.Date());
        if(evaluateService.save(suggest)){
            return ResultUtils.success("新增成功");
        }
        return ResultUtils.error("新增失败!");
    }

    /**
     * 编辑
     * @param suggest
     * @return
     */
    @PutMapping
    public ResultVo update(@RequestBody Suggest suggest){
        // 编辑时通常不更新创建时间
        if(evaluateService.updateById(suggest)){
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败!");
    }

    /**
     * 列表查询
     * @param parm
     * @return
     */
    @GetMapping("/getList")
    public ResultVo getList(Evaluate parm){
        IPage<Suggest> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        QueryWrapper<Suggest> query = new QueryWrapper<>();
        query.lambda()
                .like(StringUtils.isNotEmpty(parm.getName()), Suggest::getTitle,parm.getName())
                .or()
                .like(StringUtils.isNotEmpty(parm.getName()), Suggest::getContent,parm.getName())
                .orderByDesc(Suggest::getCreateTime);
        IPage<Suggest> list = evaluateService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 根据id查询 (包含用户信息)
     * @param sguuestId
     * @return
     */
    @GetMapping("/{sguuestId}")
    public ResultVo getById(@PathVariable("sguuestId") Integer sguuestId){
        // 使用在Service中新定义的、能获取用户名的 service 方法
        Suggest suggest = evaluateService.getByIdWithUser(sguuestId);
        return ResultUtils.success("查询成功", suggest);
    }


    /**
     * 删除
     * @param sguuestId
     * @return
     */
    @DeleteMapping("/{sguuestId}")
    public ResultVo delete(@PathVariable("sguuestId") Integer sguuestId){
        if(evaluateService.removeById(sguuestId)){
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败!");
    }
}
