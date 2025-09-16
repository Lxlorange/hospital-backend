package com.itmk.web.wxapi.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

 
@Data
public class DeptTree {
    private String name;
    private List<UserInfo> childrens = new ArrayList<>();
}
