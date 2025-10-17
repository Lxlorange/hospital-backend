package com.itmk.netSystem.journal.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
public class JournalPage {
    private Long currentPage;
    private Long pageSize;
    private String keywords;

    private String author;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private String category;
    private String status;
}