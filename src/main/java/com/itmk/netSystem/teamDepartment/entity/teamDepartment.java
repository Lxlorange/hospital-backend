package com.itmk.netSystem.teamDepartment.entity;
import lombok.Data;

 
@Data
public class teamDepartment {
    public teamDepartment(String label, Integer value) {
        this.label = label;
        this.value = value;
    }

    public teamDepartment() {
    }

    private String label;
    private Integer value;
}

