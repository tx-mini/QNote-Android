package com.ace.qnote.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ace.qnote.R;
import com.ace.qnote.base.BaseActivity;

public class ImportCourseActivity extends BaseActivity {

    TextView tvUniversity;

    @Override
    public void initParams(Bundle params) {

    }

    @Override
    public int bindLayout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        return R.layout.activity_import_course;
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public void initView(View view) {
        tvUniversity = findViewById(R.id.tv_chooseUniversity);
    }

    @Override
    public void setListener() {
        findViewById(R.id.btn_import).setOnClickListener(this);
        tvUniversity.setOnClickListener(this);
    }

    @Override
    public void widgetClick(View v) throws Exception {
        switch (v.getId()){
            case R.id.btn_import:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.tv_chooseUniversity:
                startActivity(new Intent(this,ChooseUniversityActivity.class));
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

}
