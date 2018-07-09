package com.ace.qnote.util.permission;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import com.tbruyelle.rxpermissions2.Permission;


public class ResultDefaultHandle {
    public static String getMessage(Integer requestCode) {
        switch (requestCode){
            case PermissionConstant.REQUEST_PERMISSION_IN_MAP:
                return "若不给予QNote获取位置信息权限，定位可能无法正常工作，请在 \"设置\"-\"应用管理\" 中给QNote提供相关权限";
            case PermissionConstant.REQUEST_PERMISSION_CAMERA:
                return "若不给予QNote相机权限，拍照可能无法正常工作，请在 \"设置\"-\"应用管理\" 给QNote提供相机权限";
            case PermissionConstant.REQUEST_PERMISSION_EXTERNAL_STORAGE:
                return "若不给予QNote读写权限，上传照片可能无法正常工作，请在 \"设置\"-\"应用管理\" 给QNote提供读写权限";
            default:
                return "";
        }
    }

    public static String getSettingMessage(Integer requestCode) {
        switch (requestCode){
            case PermissionConstant.REQUEST_PERMISSION_EXTERNAL_STORAGE:
                return "若不给予QNote读写权限，上传照片可能无法正常工作，请在 \"设置\"-\"应用管理\" 给QNote提供读写权限";
            case PermissionConstant.REQUEST_PERMISSION_IN_MAP:
                return "若不给予QNote获取位置信息权限，定位可能无法正常工作，请在 \"设置\"-\"应用管理\" 中给QNote提供相关权限";
            case PermissionConstant.REQUEST_PERMISSION_CAMERA:
                return "若不给予QNote相机权限，拍照可能无法正常工作，请在 \"设置\"-\"应用管理\" 给QNote提供相机权限";
            default:
                return "";
        }
    }

    public static void showToSettingDialog(final Activity activity, String message) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(activity);
        mDialog.setTitle("提示")
                .setMessage(message)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                        activity.startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public static void showSureDialog(final FragmentActivity activity,
                                       final Integer requestCode,
                                       String message,
                                       final PermissionCallBack permissionCallBack,
                                       final Permission permission) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(activity);
        mDialog.setTitle("提示")
                .setMessage(message)
                .setNegativeButton("仍要拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("给予权限", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            RxPermissionUtil.getInstance().requestPermissionEach(activity,requestCode,permissionCallBack,permission.name);
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

}
