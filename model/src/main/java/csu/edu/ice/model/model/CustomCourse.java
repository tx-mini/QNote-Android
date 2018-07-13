package csu.edu.ice.model.model;

import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

import csu.edu.ice.model.interfaze.ICourse;

/**
 * Created by ice on 2018/1/7.
 */

public class CustomCourse extends LitePalSupport implements Serializable,ICourse {

    @Column(unique = true)
    private int _id;

    private String id;
    @SerializedName("clsname")
    private String name;
    @SerializedName("location")
    private String address;
    private int startWeek;
    private int endWeek;
    private int startSection;
    private int time;
    private String teacher;
    private String duration;
    private int endSection;
    @SerializedName("day")
    private int weekday;
    private int userId;//自定义课程userId不为0
    private int backgroundColor;

    public CustomCourse() {
    }


    public CustomCourse(String id, String name, int startSection, int endSection, int weekday, int backgroundColor) {
        this.id = id;
        this.name = name;
        this.startSection = startSection;
        this.endSection = endSection;
        this.weekday = weekday;
        this.backgroundColor = backgroundColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String  getAddress() {
        if(address==null)return "";
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStartWeek() {
        return Integer.parseInt(duration.split("-")[0]);
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    public int getEndWeek() {
        return Integer.parseInt(duration.split("-")[1]);
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }

    public int getStartSection() {
        return time*2-1;
    }

    public void setStartSection(int startSection) {
        this.startSection = startSection;
    }

    public int getEndSection() {
        return time*2;
    }

    public void setEndSection(int endSection) {
        this.endSection = endSection;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null || !(obj instanceof CustomCourse)){
            return false;
        }
        if(((CustomCourse) obj).getId().equals(this.getId())){
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
