package com.ace.qnote.adapter;

import android.support.annotation.Nullable;

import com.ace.qnote.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import csu.edu.ice.model.dao.BookBean;

/**
 * Created by ice on 2018/7/9.
 */

public class DrawerNoteAdapter extends BaseQuickAdapter<BookBean,BaseViewHolder> {

    public DrawerNoteAdapter(int layoutResId, @Nullable List<BookBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookBean item) {
        helper.setText(R.id.tv_notebook,item.getValue());
    }
}
