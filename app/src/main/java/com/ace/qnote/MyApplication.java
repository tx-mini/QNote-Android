package com.ace.qnote;

import android.app.Application;

import org.litepal.LitePal;

/**
 * Created by ice on 2018/7/11.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
