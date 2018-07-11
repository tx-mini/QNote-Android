package com.ace.qnote.adapter;

import android.support.annotation.Nullable;

import com.ace.qnote.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import csu.edu.ice.model.University;

/**
 * Created by ice on 2018/7/9.
 */

public class UniversityAdapter extends BaseQuickAdapter<University,BaseViewHolder> {
    public UniversityAdapter(int layoutResId, @Nullable List<University> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, University item) {
        helper.setText(R.id.tv_university,item.getName());
    }
}
