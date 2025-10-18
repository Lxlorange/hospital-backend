package com.itmk.netSystem.phoneChat.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

 
@Data
public class DepartmentTre {
    private String name;
    private List<UserInformation> childrens = new ArrayList<>();

    /**
     * 检查该科室下是否有子分类。
     * @return 如果子分类列表（childrens）不为空，则返回true。
     */
    public boolean hasChildren() {
        return this.childrens != null && !this.childrens.isEmpty();
    }

    /**
     * 获取子分类的数量。
     * @return 子分类列表的大小。
     */
    public int getChildrenCount() {
        return this.childrens != null ? this.childrens.size() : 0;
    }

    /**
     * 添加一个新的子分类。
     * @param child 要添加的UserInformation对象。
     */
    public void addChild(UserInformation child) {
        if (this.childrens != null) {
            this.childrens.add(child);
        }
    }

    /**
     * 递归计算该科室下所有医生的总数。
     * @return 医生总人数。
     */
    public int getTotalDoctorCount() {
        if (!hasChildren()) {
            return 0;
        }
        return this.childrens.stream().mapToInt(UserInformation::getDoctorCount).sum();
    }

    /**
     * 清空所有子分类。
     */
    public void clearChildren() {
        if (this.childrens != null) {
            this.childrens.clear();
        }
    }
}
