package com.itmk.netSystem.roleWebNetMenu.entity;

import lombok.Data;

import java.util.List;

 
@Data
public class SaveMenuParm {
    private Long roleId;
    private List<Long> list;
}