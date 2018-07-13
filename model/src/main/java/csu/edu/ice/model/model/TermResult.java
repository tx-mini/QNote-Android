package csu.edu.ice.model.model;

import csu.edu.ice.model.dao.RubbishBean;
import csu.edu.ice.model.dao.TermBean;

public class TermResult {
    private RubbishBean classDir;
    private TermBean brushList;

    public RubbishBean getClassDir() {
        return classDir;
    }

    public void setClassDir(RubbishBean classDir) {
        this.classDir = classDir;
    }

    public TermBean getBrushList() {
        return brushList;
    }

    public void setBrushList(TermBean brushList) {
        this.brushList = brushList;
    }
}

