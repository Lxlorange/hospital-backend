package com.itmk.web.informationWeb.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.informationWeb.entity.SysNotice;
import com.itmk.web.informationWeb.entity.SysNoticePageParm;
import com.itmk.web.informationWeb.service.SysNoticeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

 
@RequestMapping("/api/sysNotice")
@RestController
public class SysNoticeController {
    @Autowired
    private SysNoticeService sysNoticeService;

    /**
     * 列表查询
     * @param parm
     * @return
     */
    @GetMapping("/getList")
    public ResultVo getList(SysNoticePageParm parm){
        //构造分页对象
        IPage<SysNotice> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        //构造查询条件
        QueryWrapper<SysNotice> query = new QueryWrapper<>();
        //模糊查询 like
        if(StringUtils.isNotEmpty(parm.getNoticeTitle())){
            //like查询
            query.lambda().like(SysNotice::getNoticeTitle,parm.getNoticeTitle());
        }
        //排序：order_num字段
        query.lambda().orderByDesc(SysNotice::getCreateTime);
        IPage<SysNotice> list = sysNoticeService.page(page, query);
        return ResultUtils.success("查询成功!",list);
    }

    /**
     * 新增
     * @param sysNotice
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:notice:add')")
    public ResultVo add(@RequestBody SysNotice sysNotice){
        sysNotice.setCreateTime(new Date());
        if(sysNoticeService.save(sysNotice)){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }

    /**
     * 编辑
     * @param sysNotice
     * @return
     */
    @PutMapping
    @PreAuthorize("hasAuthority('sys:notice:edit')")
    public ResultVo edit(@RequestBody SysNotice sysNotice){
        if(sysNoticeService.updateById(sysNotice)){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    //删除
    @PreAuthorize("hasAuthority('sys:notice:delete')")
    @DeleteMapping("/{noticeId}")
    public ResultVo delete(@PathVariable("noticeId") Long noticeId){
        if(sysNoticeService.removeById(noticeId)){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }
}
