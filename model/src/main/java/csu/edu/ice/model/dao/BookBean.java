package csu.edu.ice.model.dao;

import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class BookBean extends LitePalSupport implements Serializable {

    @Column(unique = true)
    private int _id;

    @SerializedName("book_id")
    private String bookId;
    private String name;
    private int term;


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String id) {
        this.bookId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    @Override
    public String toString() {
        return "BookBean{" +
                "_id=" + _id +
                ", id='" + bookId + '\'' +
                ", name='" + name + '\'' +
                ", term=" + term +
                '}';
    }
}
