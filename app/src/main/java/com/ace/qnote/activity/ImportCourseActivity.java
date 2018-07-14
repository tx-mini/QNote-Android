package com.ace.qnote.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ace.network.util.CallBack;
import com.ace.network.util.NetUtil;
import com.ace.qnote.R;
import com.ace.qnote.base.BaseActivity;
import com.ace.qnote.util.Const;

import org.litepal.LitePal;

import java.util.List;

import csu.edu.ice.model.model.CustomCourse;

import static com.ace.qnote.util.Const.CHOOSE_UNIVERISTY;
import static com.ace.qnote.util.Const.debug;

public class ImportCourseActivity extends BaseActivity {

    TextView tvUniversity;
    TextView tvSkip;
    TextView tvId;
    TextView tvPassword;

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
        tvId = findViewById(R.id.tv_id);
        tvPassword = findViewById(R.id.tv_password);
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
                if(LitePal.find(CustomCourse.class,1)!=null){
                    startActivity(new Intent(this,MainActivity.class));
                    finish();
                    return;
                }
                if(debug){
                    startActivity(new Intent(this,MainActivity.class));
                    return;
                }

                String university = tvUniversity.getText().toString();
                String id = tvId.getText().toString();
                String password = tvPassword.getText().toString();

                if(TextUtils.isEmpty(university)){
                    showToast("请先选择学校!");
                    return;
                }
                if(TextUtils.isEmpty(id)){
                    showToast("请输入学号");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    showToast("请输入密码");
                    return;
                }

                NetUtil.doRetrofitRequest(NetUtil.courseService.getCourseList(id,password,university, Const.OPEN_ID), new CallBack<List<CustomCourse>>() {


                    @Override
                    public void onSuccess(List<CustomCourse> data) {
                        LitePal.saveAll(data);
                        startActivity(new Intent(ImportCourseActivity.this,MainActivity.class));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(ImportCourseActivity.this, "发送了错误，请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String message) {

                    }
                });


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
