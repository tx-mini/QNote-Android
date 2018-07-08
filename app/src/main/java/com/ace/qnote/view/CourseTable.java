package com.ace.qnote.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ace.qnote.R;

import java.util.ArrayList;
import java.util.List;

import csu.edu.ice.model.ICourse;

public class CourseTable extends ViewGroup implements View.OnClickListener {
    private static final String TAG = "CourseLayout";
    private List<CourseView> courseViewList = new ArrayList<>();
    private List<ICourse> courseList = new ArrayList<>();
    private int width;//布局宽度
    private int height;//布局高度
    private int sectionHeight;//每节课高度
    private int sectionWidth;//每节课宽度
    private int sectionNumber = 10;//一天的节数
    private int dayNumber = 7;//一周的天数
    private int pideWidth = 0;//分隔线宽度,dp
    private int pideHeight = 0;//分隔线高度,dp
    private OnCourseClickListener onCourseClickListener;

    public CourseTable(Context context) {
        this(context, null);
    }

    public CourseTable(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CourseTable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        width = (int) (getScreenWidth() - getResources().getDimension(R.dimen.leftGridWidth));//默认宽度全屏
        height = (int) (getResources().getDimension(R.dimen.gridHeight) * sectionNumber);//默认高度600dp
        pideWidth = dip2px(2);//默认分隔线宽度2dp
        pideHeight = dip2px(2);//默认分隔线高度2dp
        sectionHeight = (int) getResources().getDimension(R.dimen.gridHeight);//计算每节课高度
        sectionWidth = (int) ((getScreenWidth() - getResources().getDimension(R.dimen.leftGridWidth)) / 7);//计算每节课宽度

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        courseViewList.clear();
        for (int i = 0; i < getChildCount(); i++) {
            CourseView child = (CourseView) getChildAt(i);
            courseViewList.add(child);//增加到list中
        }

        for (CourseView child : courseViewList) {
            int week = child.getWeek();//获得周几
            int startSection = child.getStartSection();//开始节数
            int endSection = child.getEndSection();//结束节数

            int left = sectionWidth * (week - 1) + pideWidth;//计算左边的坐标
            int right = left + sectionWidth - pideWidth;//计算右边坐标
            int top = sectionHeight * (startSection - 1) + pideHeight;//计算顶部坐标
            int bottom = top + (endSection - startSection + 1) * sectionHeight - pideHeight;//计算底部坐标

            child.layout(left, top, right, bottom);
        }

    }

    public int dip2px(float dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    public int getScreenWidth() {
        WindowManager manager = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public boolean isFree(CourseView courseView) {
        for (ICourse cours : courseList) {
            if (cours.getStartSection() <= courseView.getStartSection() && cours.getEndSection() >= courseView.getEndSection()) {
                return false;
            }
        }
        return true;
    }

    public void addCourse(ICourse customCourse) {
        courseList.add(customCourse);
        CourseView courseView = new CourseView(getContext());
        courseView.setText(customCourse.getCourseName() + "\n\n" + customCourse.getAddress());
        courseView.setCourse(customCourse);
        courseView.setBackground(getResources().getDrawable(R.drawable.shape_course_view));
        GradientDrawable shapeDrawable = (GradientDrawable) courseView.getBackground();

        shapeDrawable.setColor(customCourse.getBackgroundColor());//根据课程动态更改背景颜色
        courseView.setPadding(0, 0, 0, 0);
        courseView.setTextSize(13);
        courseView.setTextColor(Color.WHITE);
        courseView.setOnClickListener(this);

        addView(courseView);//添加到课程表

    }

    @Override
    public void onClick(View v) {
        if(onCourseClickListener!=null) onCourseClickListener.onClick(((CourseView)v).course);
    }


    public interface OnCourseClickListener{
        void onClick(ICourse customCourse);
    }

    public void setOnCourseClickListener(OnCourseClickListener onCourseClickListener){
        this.onCourseClickListener = onCourseClickListener;
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        courseList.clear();
    }

    class CourseView extends AppCompatButton {

        private static final String TAG = "CourseView";

        private ICourse course;

        public CourseView(Context context) {
            this(context, null);
        }

        public CourseView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
//        View view = LayoutInflater.from(context).inflate(R.layout.layout_course,this);
//        ButterKnife.bind(view);
        }


        public int getStartSection() {
            return course.getStartSection();
        }


        public int getEndSection() {
            return course.getEndSection();
        }

        public int getWeek() {
            return course.getWeekday();
        }

        public void setCourse(ICourse course) {
            this.course = course;
        }

    }


}