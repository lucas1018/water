package cn.zerone.water.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zero on 2018/11/30.
 */
@Entity
public class SystemMessage {
    @Id(autoincrement = true)
    Long id ;
    String content;
    String level;
    String title;
    @Unique
    Long createTime;
    public SystemMessage(String content, String title,String level, long createTime) {
        this.content = content;
        this.level = level;
        this.title = title;
        this.createTime = createTime;
    }


    @Generated(hash = 132566608)
    public SystemMessage(Long id, String content, String level, String title,
            Long createTime) {
        this.id = id;
        this.content = content;
        this.level = level;
        this.title = title;
        this.createTime = createTime;
    }


    @Generated(hash = 859060589)
    public SystemMessage() {
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }


    public String getTitle() {
        return this.title;
    }


    public void setTitle(String title) {
        this.title = title;
    }
}
