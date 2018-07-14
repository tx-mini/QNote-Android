package com.ace.qnote.adapter;

import android.support.annotation.Nullable;
import com.ace.qnote.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;

import csu.edu.ice.model.dao.TermBean;

public class TermAdapter extends BaseQuickAdapter<TermBean,BaseViewHolder> {
    public TermAdapter(int layoutResId, @Nullable List<TermBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TermBean item) {
        helper.setText(R.id.tv_university,item.getValue());
    }
}
