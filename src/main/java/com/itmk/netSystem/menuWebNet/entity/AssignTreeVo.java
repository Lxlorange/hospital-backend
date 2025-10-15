package com.itmk.netSystem.menuWebNet.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

 
@Data
public class AssignTreeVo {
    private List<SysMenu> menuList = new ArrayList<>();
    private Object[] checkList;
}
