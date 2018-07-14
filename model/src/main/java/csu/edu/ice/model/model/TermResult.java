package csu.edu.ice.model.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import csu.edu.ice.model.dao.TermBean;

public class TermResult {
    @SerializedName("term_list")
    private List<TermBean> termList;

    public List<TermBean> getTermList() {
        return termList;
    }

    public void setTermList(List<TermBean> termList) {
        this.termList = termList;
    }

}

