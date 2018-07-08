package com.ace.qnote.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ace.qnote.R;
import com.ace.qnote.view.CourseLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import csu.edu.ice.model.CustomCourse;
import csu.edu.ice.model.ICourse;

public class CourseActivity extends AppCompatActivity {

    private int nowWeek = 5;
    private View lastClickedView;
    private LinearLayout layoutWeekWrapper;
    private HorizontalScrollView scrollWeek;
    private ImageView imageArrow;
    private TextView textWeek;
    private LinearLayout layoutWeek;
    private List<ICourse> customCourseList;
    private CourseLayout courseLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        courseLayout = findViewById(R.id.courseLayout);
        layoutWeekWrapper = findViewById(R.id.layoutWeekWrapper);
        scrollWeek = findViewById(R.id.scrollWeek);
        imageArrow = findViewById(R.id.imageArrow);

        textWeek = findViewById(R.id.textWeek);
        layoutWeek = findViewById(R.id.layoutWeek);

        layoutWeek.setOnClickListener(v -> {
            if (scrollWeek.getVisibility() == View.VISIBLE) {
                scrollWeek.setVisibility(View.GONE);
                imageArrow.setRotation(0);
                courseLayout.clearCourse();
                courseLayout.addCourses(customCourseList);
            } else {
                scrollWeek.setVisibility(View.VISIBLE);
                imageArrow.setRotation(180);
            }
        });

        textWeek.setText("第"+nowWeek+"周");
        initCourseData();
        courseLayout.addCourses(customCourseList);
        initWeek();

        courseLayout.setMondayDate(new Date(System.currentTimeMillis()));
    }

    private void initCourseData() {
        int colorPink = getResources().getColor(R.color.colorAccent);
        int colorBlue = getResources().getColor(R.color.colorPrimary);

        customCourseList = new ArrayList<>();
        customCourseList.add(new CustomCourse(1,"中国近代史",1,2,1,colorBlue));
        customCourseList.add(new CustomCourse(2,"中国近代史",3,4,3,colorBlue));
        customCourseList.add(new CustomCourse(3,"编译原理",5,6,1,colorPink));
        customCourseList.add(new CustomCourse(4,"编译原理",7,8,3,colorPink));
        customCourseList.add(new CustomCourse(5,"操作系统",1,2,2,colorBlue));
        customCourseList.add(new CustomCourse(6,"操作系统",3,4,4,colorBlue));
        customCourseList.add(new CustomCourse(7,"计算机图形学",5,6,5,colorPink));
        customCourseList.add(new CustomCourse(8,"计算机图形学",3,4,2,colorPink));
        customCourseList.add(new CustomCourse(9,"计算机网络",3,4,5,colorBlue));
        customCourseList.add(new CustomCourse(10,"计算机网络",7,8,1,colorBlue));

    }


    private void initWeek() {

        for (int i = 1; i < 20; i++) {
            View view = getLayoutInflater().inflate(R.layout.item_week, null);
            TextView textWeek = view.findViewById(R.id.layoutWeek);
            textWeek.setText(i + "");
            if (i == nowWeek) {
                lastClickedView = view;
                view.setBackgroundColor(Color.WHITE);
                view.findViewById(R.id.textThisWeek).setVisibility(View.VISIBLE);
            }
            //选择了别的周
            int week = i;
            view.setOnClickListener(v -> {
                lastClickedView.setBackgroundColor(getResources().getColor(R.color.cyan));
                v.setBackgroundColor(Color.WHITE);
                lastClickedView = v;

                courseLayout.addCourses(customCourseList);
            });
            layoutWeekWrapper.addView(view);
        }
    }




}
