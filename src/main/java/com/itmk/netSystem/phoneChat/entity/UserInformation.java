package com.itmk.netSystem.phoneChat.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

 
@Data
public class UserInformation {
    private String name;
    private List<UserReverse> desc = new ArrayList<>();

    /**
     * 检查该分类下是否有医生信息。
     * @return 如果医生列表不为空，则返回true。
     */
    public boolean hasDoctors() {
        return this.desc != null && !this.desc.isEmpty();
    }

    /**
     * 获取该分类下的医生总数。
     * @return 医生列表的大小。
     */
    public int getDoctorCount() {
        return this.desc != null ? this.desc.size() : 0;
    }

    /**
     * 向列表中添加一位医生。
     * @param doctor 要添加的医生对象。
     */
    public void addDoctor(UserReverse doctor) {
        if (this.desc != null) {
            this.desc.add(doctor);
        }
    }

    /**
     * 获取所有医生的姓名列表。
     * @return 一个包含所有医生姓名的字符串列表。
     */
    public java.util.List<String> getDoctorNames() {
        if (hasDoctors()) {
            return this.desc.stream().map(UserReverse::getNickName).collect(java.util.stream.Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    /**
     * 清空医生列表。
     */
    public void clearDoctors() {
        if (this.desc != null) {
            this.desc.clear();
        }
    }
}
