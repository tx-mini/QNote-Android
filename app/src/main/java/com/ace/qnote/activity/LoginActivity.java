package com.ace.qnote.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ace.qnote.R;
import com.ace.qnote.adapter.BannerAdapter;
import com.ace.qnote.base.BaseActivity;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {
    private Tencent mTencent;
    private IUiListener loginListener = new IUiListener() {
        @Override
        public void onComplete(Object response) {
            if (null == response) {
                showToast("登录失败1");
                Log.d(TAG, "onComplete: 登录失败1");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                showToast("登录失败2");
                Log.d(TAG, "onComplete: 登录失败2");
                return;
            }
            showToast("登录成功");
            Log.d(TAG, "onComplete: "+response.toString());
            initOpenidAndToken((JSONObject) response);
            updateUserInfo();

            startActivity(new Intent(LoginActivity.this,ImportCourseActivity.class));
        }

        @Override
        public void onError(UiError uiError) {
            Log.e(TAG, "onError: "+uiError.errorDetail);
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "onCancel: 取消");
        }
    };
    private UserInfo mInfo;

    private ViewPager vpBanner;

    @Override
    public void initParams(Bundle params) {

    }

    @Override
    public int bindLayout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        return R.layout.activity_login;
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public void initView(View view) {
        vpBanner = findViewById(R.id.vp_banner);
    }

    @Override
    public void setListener() {
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void widgetClick(View v) throws Exception {
        switch (v.getId()){
            case R.id.btn_login:
//                doLogin();
                startActivity(NoteContentActivity.class);
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {
        vpBanner.setAdapter(new BannerAdapter());
    }


    public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {
     "ret": 0,
     "msg": "",
     "is_lost": 0,
     "nickname": "千山万水",
     "gender": "男",
     "province": "广东",
     "city": "肇庆",
     "year": "1997",
     "figureurl": "http:\/\/qzapp.qlogo.cn\/qzapp\/101488552\/83C38B2ADFA6400A6B18F09539FA29D3\/30",
     "figureurl_1": "http:\/\/qzapp.qlogo.cn\/qzapp\/101488552\/83C38B2ADFA6400A6B18F09539FA29D3\/50",
     "figureurl_2": "http:\/\/qzapp.qlogo.cn\/qzapp\/101488552\/83C38B2ADFA6400A6B18F09539FA29D3\/100",
     "figureurl_qq_1": "http:\/\/thirdqq.qlogo.cn\/qqapp\/101488552\/83C38B2ADFA6400A6B18F09539FA29D3\/40",
     "figureurl_qq_2": "http:\/\/thirdqq.qlogo.cn\/qqapp\/101488552\/83C38B2ADFA6400A6B18F09539FA29D3\/100",
     "is_yellow_vip": "0",
     "vip": "0",
     "yellow_vip_level": "0",
     "level": "0",
     "is_yellow_year_vip": "0"
     }
     */
    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {

                @Override
                public void onError(UiError e) {

                }

                @Override
                public void onComplete(final Object response) {
                    JSONObject json = (JSONObject)response;

                    if(json.has("figureurl")){
                        try {
                            String figureUrl = json.getString("figureurl_qq_2");
//                            Glide.with(LoginActivity.this).load(figureUrl).into((ImageView) findViewById(R.id.iv_pic));
                            String nickname =json.getString("nickname");
                            Log.d(TAG, "onComplete: "+response.toString());
                            Toast.makeText(LoginActivity.this, "欢迎"+nickname, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancel() {

                }
            };
            mInfo = new UserInfo(this, mTencent.getQQToken());
            mInfo.getUserInfo(listener);

        } else {
            Toast.makeText(this, "mTencent == null || !mTencent.isSessionValid()", Toast.LENGTH_SHORT).show();
        }
    }

    private void doLogin() {
        startActivity(new Intent(LoginActivity.this,ImportCourseActivity.class));
        finish();
/*        if(mTencent == null || !mTencent.isSessionValid()) {
            mTencent = Tencent.createInstance("101488552", this);
            mTencent.login(this, "all", loginListener, true);
        }else{
            updateUserInfo();
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
    }
}
