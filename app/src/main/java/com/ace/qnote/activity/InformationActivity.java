package com.ace.qnote.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ace.qnote.R;
import com.ace.qnote.util.Const;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        SharedPreferences sp = getSharedPreferences(Const.SP_NAME, MODE_PRIVATE);

        ((TextView)findViewById(R.id.tv_nickname)).setText(sp.getString("nickname",""));
        String imageUrl = sp.getString("imageUrl", "");

        Glide.with(this).load(imageUrl)
                .apply(new RequestOptions().circleCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher))
                .into((ImageView) findViewById(R.id.iv_image));
    }

    public void logout(View v){
        SharedPreferences sp = getSharedPreferences(Const.SP_NAME, MODE_PRIVATE);
        sp.edit().putString("openId",null).putString("nickname",null).putBoolean("autoLogin",false).commit();
        startActivity(new Intent(this,LoginActivity.class));
        setResult(Const.LOGOUT);
        finish();
    }
}
