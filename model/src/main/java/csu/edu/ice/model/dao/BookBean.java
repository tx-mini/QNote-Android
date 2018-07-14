package csu.edu.ice.model.dao;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class BookBean extends LitePalSupport implements Serializable {

    @Column(unique = true)
    private int _id;

    private String id;
    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
