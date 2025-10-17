package com.itmk.netSystem.announceWeb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
public class AnnounceWebPage {
    private Long currentPage;
    private Long pageSize;
    private String noticeTitle;

    private String author;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private String status;
    private String contentKeyword;
}