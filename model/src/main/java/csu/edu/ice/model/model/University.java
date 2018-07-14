package csu.edu.ice.model.model;

import org.litepal.crud.LitePalSupport;

/**
 * Created by ice on 2018/7/11.
 */

public class University extends LitePalSupport {
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
