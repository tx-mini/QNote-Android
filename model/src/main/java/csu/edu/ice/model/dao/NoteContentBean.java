package csu.edu.ice.model.dao;

import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class NoteContentBean  extends LitePalSupport implements Serializable {
    @Column(unique = true)
    private int _id;

    @SerializedName("book_ref")
    private String bookRef;

    @SerializedName("note_id")
    private String noteId;

    private String content;

    @SerializedName("recent_time")
    private String recentTime;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getBookRef() {
        return bookRef;
    }

    public void setBookRef(String bookRef) {
        this.bookRef = bookRef;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecentTime() {
        return recentTime;
    }

    public void setRecentTime(String recentTime) {
        this.recentTime = recentTime;
    }
}
