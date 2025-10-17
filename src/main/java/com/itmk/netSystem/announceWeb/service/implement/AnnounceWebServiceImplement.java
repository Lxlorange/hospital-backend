package com.itmk.netSystem.announceWeb.service.implement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.announceWeb.entity.SysNotice;
import com.itmk.netSystem.announceWeb.mapper.AnnounceWebMapper;
import com.itmk.netSystem.announceWeb.service.AnnounceWebService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AnnounceWebServiceImplement extends ServiceImpl<AnnounceWebMapper, SysNotice> implements AnnounceWebService {
    @Override
    public SysNotice getNoticeById(Long noticeId) {
        return this.baseMapper.selectById(noticeId);
    }

    @Override
    public boolean publishNotice(Long noticeId) {
        UpdateWrapper<SysNotice> updateWrapper = new UpdateWrapper<>();
        return this.update(updateWrapper);
    }

    @Override
    public boolean archiveNotice(Long noticeId) {
        UpdateWrapper<SysNotice> updateWrapper = new UpdateWrapper<>();
        return this.update(updateWrapper);
    }



    @Override
    public List<SysNotice> getLatestNotices(int count) {
        QueryWrapper<SysNotice> query = new QueryWrapper<>();
        query.lambda().orderByDesc(SysNotice::getCreateTime).last("LIMIT " + count);
        return this.baseMapper.selectList(query);
    }
}
