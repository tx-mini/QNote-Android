package com.ace.qnote.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ace.qnote.R;
import com.ace.qnote.adapter.UniversityAdapter;
import com.ace.qnote.base.BaseActivity;

import java.util.Arrays;

public class ChooseUniversityActivity extends BaseActivity {

    RecyclerView rvUniversity;
    UniversityAdapter universityAdapter;

    @Override
    public void initParams(Bundle params) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_choose_university;
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public void initView(View view) {
        rvUniversity = findViewById(R.id.rv_university);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void widgetClick(View v) throws Exception {

    }

    @Override
    public void doBusiness(Context mContext) {
        String[] universityList = {"中南大学","北京大学","清华大学"};
        universityAdapter = new UniversityAdapter(R.layout.item_text_line, Arrays.asList(universityList));
        rvUniversity.setLayoutManager(new LinearLayoutManager(this));
        rvUniversity.setAdapter(universityAdapter);
    }
}
