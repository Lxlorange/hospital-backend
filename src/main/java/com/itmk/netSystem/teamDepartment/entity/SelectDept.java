package com.itmk.netSystem.teamDepartment.entity;
import lombok.Data;

 
@Data
public class SelectDept {
    public SelectDept(String label, Integer value) {
        this.label = label;
        this.value = value;
    }

    public SelectDept() {
    }

    private String label;
    private Integer value;
}

