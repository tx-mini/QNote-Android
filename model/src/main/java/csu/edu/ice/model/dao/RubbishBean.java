package csu.edu.ice.model.dao;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;


public class RubbishBean extends LitePalSupport implements Serializable {
    @Column(unique = true)
    private int _id;

    private String book_ref;
    private String note_id;
    private String openid;
    private String content;
    private String recent_time;
    private String is_rubbish;
    private Object name;
    private String is_imp;


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getBook_ref() {
        return book_ref;
    }

    public void setBook_ref(String book_ref) {
        this.book_ref = book_ref;
    }

    public String getNote_id() {
        return note_id;
    }

    public void setNote_id(String note_id) {
        this.note_id = note_id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecent_time() {
        return recent_time;
    }

    public void setRecent_time(String recent_time) {
        this.recent_time = recent_time;
    }

    public String getIs_rubbish() {
        return is_rubbish;
    }

    public void setIs_rubbish(String is_rubbish) {
        this.is_rubbish = is_rubbish;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public String getIs_imp() {
        return is_imp;
    }

    public void setIs_imp(String is_imp) {
        this.is_imp = is_imp;
    }
}
