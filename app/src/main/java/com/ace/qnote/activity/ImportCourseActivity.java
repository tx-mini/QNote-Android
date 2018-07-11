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

import static com.ace.qnote.util.Const.CHOOSE_UNIVERISTY;

public class ImportCourseActivity extends BaseActivity {

    TextView tvUniversity;
    TextView tvSkip;

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
        tvSkip = findViewById(R.id.tv_skip);
    }

    @Override
    public void setListener() {
        findViewById(R.id.btn_import).setOnClickListener(this);
        tvUniversity.setOnClickListener(this);
        tvSkip.setOnClickListener(this);
    }

    @Override
    public void widgetClick(View v) throws Exception {
        switch (v.getId()){
            case R.id.btn_import:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.tv_chooseUniversity:
                startActivityForResult(new Intent(this,ChooseUniversityActivity.class),CHOOSE_UNIVERISTY);
                break;
            case R.id.tv_skip:
                startActivity(new Intent(this,MainActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHOOSE_UNIVERISTY && resultCode==200){
            String universityName = data.getStringExtra("universityName");
            if(universityName!=null){
                tvUniversity.setText(universityName);
            }
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

}
