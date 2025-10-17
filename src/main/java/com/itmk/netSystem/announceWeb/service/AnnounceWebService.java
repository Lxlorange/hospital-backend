package com.itmk.netSystem.announceWeb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.netSystem.announceWeb.entity.SysNotice;

import java.util.List;


public interface AnnounceWebService extends IService<SysNotice> {

    SysNotice getNoticeById(Long noticeId);

    boolean publishNotice(Long noticeId);


    boolean archiveNotice(Long noticeId);


    List<SysNotice> getLatestNotices(int count);
}
