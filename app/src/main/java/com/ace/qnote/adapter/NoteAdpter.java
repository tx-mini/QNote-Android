package com.ace.qnote.adapter;

import android.support.annotation.Nullable;
import android.view.Gravity;

import com.ace.qnote.R;
import com.ace.qnote.util.CommonUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zhouwei.library.CustomPopWindow;

import java.util.List;

/**
 * Created by ice on 2018/7/10.
 */

public class NoteAdpter extends BaseQuickAdapter<String,BaseViewHolder> {
    public NoteAdpter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_note_title,item);

        helper.getView(R.id.layout_root)
                .setOnLongClickListener(v -> {
                    CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                            .setView(R.layout.layout_pop_menu)//显示的布局
                            .create()//创建PopupWindow
                            .showAsDropDown(v,500,10, Gravity.CENTER);//显示PopupWindow
                    return false;
                });
    }

}
