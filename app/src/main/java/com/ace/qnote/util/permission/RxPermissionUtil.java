package com.ace.qnote.util.permission;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;


public class RxPermissionUtil {

    private PermissionCallBack permissionCallBack = null;
    private static RxPermissionUtil instance;

    private RxPermissionUtil() {

    }

    public static RxPermissionUtil getInstance() {
        if (instance == null){
            return new RxPermissionUtil();
        }else
            return instance;
    }

    /**
     *  在CallBack函数返回之前。已经对相应情况做出相应处理，如果不需要特殊处理，则回调函数中不需要编写代码
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermissionEach(final FragmentActivity activity, final Integer requestCode, PermissionCallBack m_permissionCallBack, final String... PERMISSION){
        this.permissionCallBack = m_permissionCallBack;
        RxPermissions rxPermissions = new RxPermissions(activity); // where this is an Activity instance
        rxPermissions
                .requestEach(PERMISSION)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted){
                            if (permissionCallBack!=null)
                                permissionCallBack.onSuccess(permission.name);
                        }else if (permission.shouldShowRequestPermissionRationale){
                            if (permissionCallBack!=null) {
                                permissionCallBack.onNeedApplyAgain(activity,permission,requestCode);
                            }
                        }else{
                            if (permissionCallBack!=null)
                                permissionCallBack.onNeedToSetting(activity,permission,requestCode);
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermission(final FragmentActivity activity, final ActionCallBackListener actionCallBackListener, final String... PERMISSION){
        RxPermissions rxPermissions = new RxPermissions(activity); // where this is an Activity instance
        rxPermissions
                .request(PERMISSION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            if (actionCallBackListener!=null)
                                actionCallBackListener.onSuccess("success");
                        } else{
                            if (actionCallBackListener!=null)
                                actionCallBackListener.onFailure("permission refuse","maybe one of the permission refuse");
                        }
                    }
                });
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private int doAppOpsCheck(Activity activity, Permission permission) {
        AppOpsManager appOpsManager = (AppOpsManager) activity.getSystemService(Context.APP_OPS_SERVICE);
        switch (permission.name){
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_FINE_LOCATION, Process.myUid(), activity.getPackageName());
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_WRITE_EXTERNAL_STORAGE, Process.myUid(), activity.getPackageName());
                }
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_READ_EXTERNAL_STORAGE, Process.myUid(), activity.getPackageName());
                }
            case Manifest.permission.CAMERA:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_CAMERA, Process.myUid(), activity.getPackageName());
                }
        }
        return -1;
    }

}
