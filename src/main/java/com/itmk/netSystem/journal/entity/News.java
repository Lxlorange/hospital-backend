package com.itmk.netSystem.journal.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("news")
public class News implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private String image;
    private String textDesc;
    private String textContent;
    private String toIndex;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getImage() { return image; }
    public String getTextDesc() { return textDesc; }
    public String getTextContent() { return textContent; }
    public String getToIndex() { return toIndex; }
    public Date getCreateTime() { return createTime; }

    public void setId(Integer id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setImage(String image) { this.image = image; }
    public void setTextDesc(String textDesc) { this.textDesc = textDesc; }
    public void setTextContent(String textContent) { this.textContent = textContent; }
    public void setToIndex(String toIndex) { this.toIndex = toIndex; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", textDesc='" + textDesc + '\'' +
                ", textContent='" + textContent + '\'' +
                ", toIndex='" + toIndex + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}