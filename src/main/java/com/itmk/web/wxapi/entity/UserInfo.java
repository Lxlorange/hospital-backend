package com.itmk.web.wxapi.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

 
@Data
public class UserInfo {
    private String name;
    private List<UserDesc> desc = new ArrayList<>();
}
