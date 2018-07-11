package com.ace.qnote.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ace.qnote.R;
import com.ace.qnote.util.CommonUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zhouwei.library.CustomPopWindow;

import java.util.List;

/**
 * Created by ice on 2018/7/10.
 */

public class NoteAdapter extends BaseQuickAdapter<String,BaseViewHolder> {
    Activity activity;

    public NoteAdapter(int layoutResId, @Nullable List<String> data,Activity activity) {
        super(layoutResId, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_note_title,item);

        helper.getView(R.id.layout_root)
                .setOnLongClickListener(v -> {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_menu,null);
                    TextView tvDelete = view.findViewById(R.id.tv_delete);



                    int xOffset = (int) (CommonUtils.getScreenWidth(activity)*0.45);
                    CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                            .setView(view)//显示的布局
                            .create()//创建PopupWindow
                            .showAsDropDown(v,xOffset,-CommonUtils.dip2px(mContext,25), Gravity.CENTER);//显示PopupWindow
                    tvDelete.setOnClickListener(v1 -> {
                        popWindow.dissmiss();
                        CustomPopWindow popDeleteWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                                .setView(R.layout.layout_pop_delete_note)//显示的布局
                                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                                .setBgDarkAlpha(0.7f) // 控制亮度
                                .create()//创建PopupWindow
                                .showAtLocation(v, Gravity.CENTER,0,0);//显示PopupWindow
                    });
                    return false;
                });
    }

}
