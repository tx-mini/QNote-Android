package csu.edu.ice.model;

/**
 * Created by ice on 2018/7/7.
 */

public interface ICourse{
    int getStartSection();
    int getEndSection();
    int getWeekday();

    String getCourseName();

    String getAddress();

    int getBackgroundColor();
}