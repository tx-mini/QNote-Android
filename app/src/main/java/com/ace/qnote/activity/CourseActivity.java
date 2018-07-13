package com.ace.qnote.activity;

import android.content.Intent;
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

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import csu.edu.ice.model.model.CustomCourse;
import csu.edu.ice.model.interfaze.ICourse;

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
        findViewById(R.id.iv_add_course).setOnClickListener(v-> addCourse());

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

    private void addCourse() {
        startActivity(new Intent(this,AddEventActivity.class));
    }

    private void initCourseData() {
        int colorPink = getResources().getColor(R.color.colorAccent);
        int colorBlue = getResources().getColor(R.color.colorPrimary);

        int[] colors ={colorBlue, colorPink};

        customCourseList = new ArrayList<>();
        List<CustomCourse> courseList = LitePal.findAll(CustomCourse.class);
        Random random = new Random(2);
        for (CustomCourse customCourse : courseList) {
            customCourse.setBackgroundColor(colors[random.nextInt(10)%colors.length]);
        }
        customCourseList.addAll(courseList);

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
