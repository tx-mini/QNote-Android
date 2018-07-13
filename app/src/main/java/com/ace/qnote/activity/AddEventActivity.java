package com.ace.qnote.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ace.qnote.R;
import com.ace.qnote.base.BaseActivity;
import com.ace.qnote.fragment.ChooseCourseWeekFragment;
import com.ace.qnote.fragment.ChooseTimeFragment;

import csu.edu.ice.model.ICourse;

import static com.ace.qnote.util.Const.weekdays;

public class AddEventActivity extends BaseActivity {

    EditText etName;
    EditText etAddress;
    TextView tvChooseWeek;
    Button btnAdd;
    TextView tvChooseCourseTime;

    ICourse customCourse;
    Button btnDelete;

    String type = "添加课程";//添加课程  学校课程  自定义课程  活动
    private int startSection;
    private int weekday;

    @Override
    public void initParams(Bundle params) {
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_add_event;
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public void initView(View view) {
        etName = findViewById(R.id.et_name);
        etAddress = findViewById(R.id.et_address);
        tvChooseWeek = findViewById(R.id.tv_choose_week);
        tvChooseCourseTime = findViewById(R.id.tv_choose_course_time);
        btnAdd = findViewById(R.id.btn_add);
        btnDelete = findViewById(R.id.btn_delete);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void widgetClick(View v) throws Exception {

    }

    @Override
    public void doBusiness(Context mContext) {

    }


    public void viewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_choose_week:
                ChooseCourseWeekFragment fragment = new ChooseCourseWeekFragment();
                fragment.show(getSupportFragmentManager(), "chooseCourseWeek");
                fragment.setCallback((startWeek, endWeek) -> {
                    tvChooseWeek.setText("第" + startWeek + "周 到第" + endWeek + "周");
//                    customCourse.setStartWeek(startWeek);
//                    customCourse.setEndWeek(endWeek);
                });
                break;
            case R.id.tv_choose_course_time:
                ChooseTimeFragment chooseTimeFragment = new ChooseTimeFragment();
                chooseTimeFragment.show(getSupportFragmentManager(), "chooseCourseTime");
                chooseTimeFragment.setCallback((weekday, startSection, endSection) -> {
//                    customCourse.setWeekday(weekday);
//                    customCourse.setStartSection(startSection);
//                    customCourse.setEndSection(endSection);
                    if (startSection != endSection) {
                        tvChooseCourseTime.setText("周" + weekdays[weekday - 1] + " " + startSection + "-" + endSection + "节");
                    } else {
                        tvChooseCourseTime.setText("周" + weekdays[weekday - 1] + " 第" + startSection + "节");
                    }
                });
                break;

            case R.id.btn_delete:
                //删除自定义课程
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定要删除吗？删除后无法恢复")
                        .setCancelable(true)
                        .setPositiveButton("确定", (dialog, which) -> {
                            deleteCourse();//删除课程
                        })
                        .setNegativeButton("取消",null)
                        .create()
                        .show();

                break;
        }
    }

    private void deleteCourse() {
    }

    private void setReadable(){
        etName.setEnabled(false);
        etAddress.setEnabled(false);
        tvChooseWeek.setClickable(false);
        tvChooseCourseTime.setClickable(false);
        btnAdd.setText("修改");
        btnDelete.setVisibility(View.VISIBLE);
    }

    private void setWritable(){
        etName.setEnabled(true);
        etAddress.setEnabled(true);
        tvChooseWeek.setClickable(true);
        tvChooseCourseTime.setClickable(true);
        btnAdd.setText("保存");
        btnDelete.setVisibility(View.GONE);
    }

}
