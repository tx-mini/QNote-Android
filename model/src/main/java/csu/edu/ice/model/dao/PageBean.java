package csu.edu.ice.model.dao;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.sql.Timestamp;

public class PageBean extends LitePalSupport implements Serializable {
    @Column(unique = true)
    private int _id;

    private String id;
    private String title;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Boolean isKeyNote;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getKeyNote() {
        return isKeyNote;
    }

    public void setKeyNote(Boolean keyNote) {
        isKeyNote = keyNote;
    }
}
