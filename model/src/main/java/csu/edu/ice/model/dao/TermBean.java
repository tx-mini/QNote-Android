package csu.edu.ice.model.dao;


import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.List;


public class TermBean extends LitePalSupport implements Serializable {
    @Column(unique = true)
    private int _id;
    private String term;

    @Column(ignore = true)
    private List<BookBean> children;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public List<BookBean> getChildren() {
        return children;
    }

    public void setChildren(List<BookBean> children) {
        this.children = children;
    }
}
