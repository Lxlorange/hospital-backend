package com.itmk.netSystem.announceWeb.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.netSystem.announceWeb.entity.AnnounceWebPage;
import com.itmk.netSystem.announceWeb.entity.SysNotice;
import com.itmk.netSystem.announceWeb.service.AnnounceWebService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RequestMapping("/api/sysNotice")
@RestController
public class AnnounceWebController {
    @Autowired
    private AnnounceWebService announceWebService;



    @GetMapping("/getList")
    public ResultVo getList(AnnounceWebPage parm){
        IPage<SysNotice> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        QueryWrapper<SysNotice> query = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(parm.getNoticeTitle())){
            query.lambda().like(SysNotice::getNoticeTitle,parm.getNoticeTitle());
        }
        query.lambda().orderByDesc(SysNotice::getCreateTime);
        IPage<SysNotice> list = announceWebService.page(page, query);
        return ResultUtils.success("查询成功!",list);
    }

    @GetMapping("/{noticeId}")
    public ResultVo getNoticeById(@PathVariable("noticeId") Long noticeId) {
        SysNotice notice = announceWebService.getById(noticeId);
        if (notice != null) {
            return ResultUtils.success("查询成功!", notice);
        }
        return ResultUtils.error("查询失败, 公告不存在!");
    }



    @PreAuthorize("hasAuthority('sys:notice:publish')")
    @PostMapping("/publish/{noticeId}")
    public ResultVo publishNotice(@PathVariable("noticeId") Long noticeId) {
        SysNotice notice = announceWebService.getById(noticeId);
        if (notice != null) {
            return ResultUtils.success("公告发布成功!");
        }
        return ResultUtils.error("公告发布失败!");
    }


    @PutMapping
    @PreAuthorize("hasAuthority('sys:notice:edit')")
    public ResultVo edit(@RequestBody SysNotice sysNotice){
        if(announceWebService.updateById(sysNotice)){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:notice:add')")
    public ResultVo add(@RequestBody SysNotice sysNotice){
        sysNotice.setCreateTime(new Date());
        if(announceWebService.save(sysNotice)){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }

    @PreAuthorize("hasAuthority('sys:notice:delete')")
    @DeleteMapping("/batch")
    public ResultVo deleteBatch(@RequestBody List<Long> ids){
        if(announceWebService.removeByIds(ids)){
            return ResultUtils.success("批量删除成功!");
        }
        return ResultUtils.error("批量删除失败!");
    }

    @GetMapping("/latest/{count}")
    public ResultVo getLatestNotices(@PathVariable("count") int count) {
        QueryWrapper<SysNotice> query = new QueryWrapper<>();
        query.lambda().orderByDesc(SysNotice::getCreateTime).last("LIMIT " + count);
        List<SysNotice> list = announceWebService.list(query);
        return ResultUtils.success("查询最新公告成功!", list);
    }

    @PreAuthorize("hasAuthority('sys:notice:delete')")
    @DeleteMapping("/{noticeId}")
    public ResultVo delete(@PathVariable("noticeId") Long noticeId){
        if(announceWebService.removeById(noticeId)){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }

    @GetMapping("/count")
    public ResultVo getTotalCount() {
        long count = announceWebService.count();
        return ResultUtils.success("查询总数成功!", count);
    }


}
