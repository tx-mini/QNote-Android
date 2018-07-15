package com.ace.qnote.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ace.qnote.R;
import com.ace.qnote.util.CommonUtils;
import com.ace.qnote.util.Const;
import com.ace.qnote.view.CourseLayout;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import csu.edu.ice.model.dao.TermBean;
import csu.edu.ice.model.interfaze.ICourse;
import csu.edu.ice.model.model.CustomCourse;

public class CourseActivity extends AppCompatActivity {

    private static final String TAG = "CourseActivity";
    private int nowWeek = 5;
    private View lastClickedView;
    private LinearLayout layoutWeekWrapper;
    private HorizontalScrollView scrollWeek;
    private ImageView imageArrow;
    private TextView tvTerm;
    private TextView tvWeek;
    private LinearLayout layoutWeek;
    private List<ICourse> customCourseList;
    private CourseLayout courseLayout;
    private String term;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        courseLayout = findViewById(R.id.courseLayout);
        layoutWeekWrapper = findViewById(R.id.layoutWeekWrapper);
        scrollWeek = findViewById(R.id.scrollWeek);
        imageArrow = findViewById(R.id.imageArrow);
        tvWeek = findViewById(R.id.tv_week);
        tvTerm = findViewById(R.id.tv_term);
        List<TermBean> termList = LitePal.findAll(TermBean.class);
        tvTerm.setText(Const.termToChinese[termList.get(termList.size()-1).getTerm()]);
        layoutWeek = findViewById(R.id.layoutWeek);
        findViewById(R.id.iv_add_course).setOnClickListener(v-> addCourse());
        nowWeek = CommonUtils.getWeek(Const.START_DAY);
        layoutWeek.setOnClickListener(v -> {
            if (scrollWeek.getVisibility() == View.VISIBLE) {
                scrollWeek.setVisibility(View.GONE);
                imageArrow.setRotation(0);
                courseLayout.clearCourse();
                courseLayout.addCourses(getCoursesByWeek(nowWeek));
            } else {
                scrollWeek.setVisibility(View.VISIBLE);
                imageArrow.setRotation(180);
            }
        });
        tvWeek.setText("第"+ nowWeek +"周");
        initCourseData();
        courseLayout.addCourses(getCoursesByWeek(nowWeek));
        initWeek();
        Calendar calendar = Calendar.getInstance();

        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        Log.d(TAG, "onCreate: "+weekday);
        int diff = weekday - 2;
        if(diff<0){
            diff = 6;
        }
        calendar.add(Calendar.DAY_OF_WEEK,-diff);
        courseLayout.setMondayDate(calendar.getTime());
    }

    private void addCourse() {
        startActivity(new Intent(this,AddEventActivity.class));
    }

    private void initCourseData() {
        int colorPink = getResources().getColor(R.color.colorAccent);
        int colorBlue = getResources().getColor(R.color.colorPrimary);

        int[] colors ={colorBlue, colorPink};

        customCourseList = new ArrayList<>();
        List<CustomCourse> courseList = LitePal.where("term = ?","4").find(CustomCourse.class);
        for (CustomCourse customCourse : courseList) {
            customCourse.setBackgroundColor(colors[Math.abs(customCourse.getName().hashCode()%colors.length)]);
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

                courseLayout.addCourses(getCoursesByWeek(week));

            });
            layoutWeekWrapper.addView(view);
        }
    }


    private List<ICourse> getCoursesByWeek(int week){

        List<ICourse> courses = new LinkedList<>();
        for (int j = 0; j < customCourseList.size(); j++) {
            CustomCourse course = (CustomCourse) customCourseList.get(j);
            if(course.getStartWeek()<=week && course.getEndWeek()>=week) {
                courses.add(course);
            }
        }
        return courses;
    }


}
