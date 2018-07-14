package com.ace.qnote.adapter;

import android.widget.ImageView;

import com.ace.qnote.R;
import com.ace.qnote.util.Const;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import csu.edu.ice.model.model.SimpleContentBean;

public class NoteContentEditAdapter extends BaseItemDraggableAdapter<SimpleContentBean,BaseViewHolder> {
    public NoteContentEditAdapter(List<SimpleContentBean> data) {
        super(data);
    }

    public NoteContentEditAdapter(int layoutResId, List<SimpleContentBean> data) {
        super(layoutResId, data);
    }



    @Override
    protected void convert(BaseViewHolder helper, SimpleContentBean item) {
        if (Const.CONTENT_TYPE_IMG==item.getType()){
            helper.setGone(R.id.tv_text_edit,false);
            helper.setGone(R.id.iv_img_edit,true);
            Glide.with(mContext).load(item.getContent()).into((ImageView) helper.getView(R.id.iv_img_edit));
        }else if (Const.CONTENT_TYPE_TEXT==item.getType()){
            helper.setText(R.id.tv_text_edit, item.getContent());
            helper.setGone(R.id.iv_img_edit,false);
            helper.setGone(R.id.tv_text_edit,true);
            helper.addOnClickListener(R.id.tv_text_edit);
        }
    }
}
