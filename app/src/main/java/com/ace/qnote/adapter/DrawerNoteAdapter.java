package com.ace.qnote.adapter;

import android.support.annotation.Nullable;

import com.ace.qnote.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by ice on 2018/7/9.
 */

public class DrawerNoteAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    public DrawerNoteAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_notebook,item);
    }
}
