package csu.edu.ice.model.model;

import java.util.ArrayList;
import java.util.List;

import csu.edu.ice.model.dao.RubbishBean;
import csu.edu.ice.model.dao.TermBean;

public class TermResult {
    private List<TermBean> classDir;
    private List<RubbishBean> brushList;

    public List<TermBean> getClassDir() {
        return classDir;
    }

    public void setClassDir(List<TermBean> classDir) {
        this.classDir = classDir;
    }

    public List<RubbishBean> getBrushList() {
        return brushList;
    }

    public void setBrushList(List<RubbishBean> brushList) {
        this.brushList = brushList;
    }
}

