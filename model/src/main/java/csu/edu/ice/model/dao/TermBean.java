package csu.edu.ice.model.dao;


import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.List;


public class TermBean extends LitePalSupport implements Serializable {
    @Column(unique = true)
    private int _id;

    private String id;
    private String value;

    @Column(ignore = true)
    private List<BookBean> childrens;

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

    public List<BookBean> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<BookBean> childrens) {
        this.childrens = childrens;
    }
}
