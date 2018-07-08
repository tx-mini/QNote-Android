package com.ace.qnote.adapter;

import android.widget.ImageView;

import com.ace.qnote.R;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class NoteContentAdapter extends BaseItemDraggableAdapter<String,BaseViewHolder> {


    public NoteContentAdapter(List<String> data) {
        super(data);
    }

    public NoteContentAdapter(int layoutResId, List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        if (item.charAt(0)=='h'){
            helper.setGone(R.id.tv_text,false);
            helper.setGone(R.id.iv_img,true);
            Glide.with(mContext).load(item).into((ImageView) helper.getView(R.id.iv_img));
        }else {
            helper.setText(R.id.tv_text, item);
            helper.setGone(R.id.iv_img,false);
            helper.setGone(R.id.tv_text,true);
        }
    }

}
