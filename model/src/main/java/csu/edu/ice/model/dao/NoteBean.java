package csu.edu.ice.model.dao;

import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class NoteBean extends LitePalSupport implements Serializable {
    @Column(unique = true)
    private int _id;

    @SerializedName("note_id")
    private String id;
    private String name;

    @SerializedName("is_imp")
    private int isKeyNote;

    @SerializedName("is_rubbish")
    private int isRubbish;

    private String content;

    @SerializedName("recent_time")
    private String recentTime;

    @SerializedName("book_ref")
    private String bookRef;

    @SerializedName("openid")
    private String openId;


    public NoteBean(){}

    public NoteBean(String title) {
        this.name = title;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBookRef() {
        return bookRef;
    }

    public void setBookRef(String bookRef) {
        this.bookRef = bookRef;
    }

    public String getRecentTime() {
        return recentTime;
    }

    public void setRecentTime(String recentTime) {
        this.recentTime = recentTime;
    }


    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getIsKeyNote() {
        return isKeyNote;
    }

    public void setIsKeyNote(int isKeyNote) {
        this.isKeyNote = isKeyNote;
    }

    public int getIsRubbish() {
        return isRubbish;
    }

    public void setIsRubbish(int isRubbish) {
        this.isRubbish = isRubbish;
    }
}
