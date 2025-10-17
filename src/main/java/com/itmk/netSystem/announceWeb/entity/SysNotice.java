package com.itmk.netSystem.announceWeb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
@TableName("sys_notice")
public class SysNotice {

    @TableId(type = IdType.AUTO)
    private Integer noticeId;
    private String noticeTitle;
    private String noticeText;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;


    public Integer getNoticeId() {
        return noticeId;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public String getNoticeText() {
        return noticeText;
    }


    public void setNoticeId(Integer noticeId) {
        this.noticeId = noticeId;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public void setNoticeText(String noticeText) {
        this.noticeText = noticeText;
    }


    public String toString() {
        return "SysNotice{" +
                "noticeId=" + noticeId +
                ", noticeTitle='" + noticeTitle + '\'' +
                ", noticeText='" + noticeText + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
