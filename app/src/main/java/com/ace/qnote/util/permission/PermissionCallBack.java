package com.ace.qnote.util.permission;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.Permission;

import static com.ace.qnote.util.permission.ResultDefaultHandle.getMessage;
import static com.ace.qnote.util.permission.ResultDefaultHandle.showSureDialog;
import static com.ace.qnote.util.permission.ResultDefaultHandle.showToSettingDialog;



public class PermissionCallBack {

    PermissionCallBack permissionCallBack = null;

    public PermissionCallBack() {
        permissionCallBack = this;
    }

    /**
     * 成功时调用
     *
     * @param permissionName 相应权限的名字
     */
    public void onSuccess(String permissionName){

    }

    /**
     * 需要再次申请权限时调用
     * @param activity
     * @param permission
     */
    public void onNeedApplyAgain(FragmentActivity activity, Permission permission, Integer requestCode){
        showSureDialog(activity,requestCode, getMessage(requestCode),permissionCallBack,permission);
    }

    /**
     * 需要用户去设置中修改
     * @param activity
     * @param permission
     */
    public void onNeedToSetting(FragmentActivity activity,Permission permission,Integer requestCode){
        showToSettingDialog(activity,getMessage(requestCode));
    }



}
