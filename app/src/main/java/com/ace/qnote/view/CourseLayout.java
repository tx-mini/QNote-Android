package com.ace.qnote.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ace.qnote.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import csu.edu.ice.model.interfaze.ICourse;

/**
 * Created by ice on 2018/7/7.
 */

public class CourseLayout extends RelativeLayout {

    private LinearLayout topWeek;
    private LinearLayout leftTime;
    private WeekdayViewHolder[] viewdayHolders = new WeekdayViewHolder[7];
    private CourseTable courseTable;

    public CourseLayout(Context context) {
        this(context,null);
    }

    public CourseLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CourseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_course, this);//加载布局文件
        topWeek = findViewById(R.id.top_week);
        leftTime = findViewById(R.id.left_time);
        courseTable = findViewById(R.id.courseTable);

        initLeftTime();
        initWeekday();
    }

    private void initLeftTime() {
        for (int i = 0; i < 10; i++) {
            TextView textView = new TextView(getContext());
            textView.setHeight((int) getResources().getDimension(R.dimen.gridHeight));
            textView.setText((i + 1) + "");
            textView.setGravity(Gravity.CENTER);
            leftTime.addView(textView);
        }
    }

    private void initWeekday() {
        viewdayHolders[0] = getWeekView(1, "周一");
        viewdayHolders[1] = getWeekView(2, "周二");
        viewdayHolders[2] = getWeekView(3, "周三");
        viewdayHolders[3] = getWeekView(4, "周四");
        viewdayHolders[4] = getWeekView(5, "周五");
        viewdayHolders[5] = getWeekView(6, "周六");
        viewdayHolders[6] = getWeekView(7, "周日");
        for (int i = 0; i < viewdayHolders.length; i++) {
            topWeek.addView(viewdayHolders[i].root);
        }
    }

    private WeekdayViewHolder getWeekView(int day, String week) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.week_layout, topWeek, false);
        TextView tvTop = view.findViewById(R.id.top);
        tvTop.setText(week);
        TextView tvBootom = view.findViewById(R.id.bottom);
        tvBootom.setText(day + "");
        WeekdayViewHolder weekdayViewHolder = new WeekdayViewHolder();
        weekdayViewHolder.root = view;
        weekdayViewHolder.tvDate = tvBootom;
        return weekdayViewHolder;
    }


    class WeekdayViewHolder{
        public View root;
        public TextView tvDate;
        public TextView tvWeekday;
    }

    public void setWeekday(int weekday){
        viewdayHolders[weekday].root.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        viewdayHolders[weekday].tvDate.setTextColor(Color.WHITE);
        viewdayHolders[weekday].tvWeekday.setTextColor(Color.WHITE);
    }

    public void setMondayDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        for (WeekdayViewHolder viewdayHolder : viewdayHolders) {
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            viewdayHolder.tvDate.setText(day+"");
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }
    }

    public void addCourse(ICourse course){
        courseTable.addCourse(course);
    }

    public void addCourses(List<ICourse> courses){
        courseTable.removeAllViews();
        if (courses == null) return;

        for (ICourse customCourse : courses) {
            courseTable.addCourse(customCourse);
        }
        courseTable.requestLayout();
    }

    public void clearCourse(){
        courseTable.removeAllViews();
    }

    public void setOnCourseClickListener(CourseTable.OnCourseClickListener onCourseClickListener){
        courseTable.setOnCourseClickListener(onCourseClickListener);
    }

}
