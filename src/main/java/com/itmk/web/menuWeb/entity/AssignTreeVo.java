package com.itmk.web.menuWeb.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

 
@Data
public class AssignTreeVo {
    private List<SysMenu> menuList = new ArrayList<>();
    private Object[] checkList;
}
