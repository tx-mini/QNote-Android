package com.ace.qnote.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ace.qnote.R;
import com.ace.qnote.util.CommonUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zhouwei.library.CustomPopWindow;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ice on 2018/7/10.
 */

public class NoteAdapter extends BaseQuickAdapter<String,BaseViewHolder> {
    Activity activity;
    View rootView;
    private int moveToIndex;

    public NoteAdapter(int layoutResId, @Nullable List<String> data,Activity activity,View rootView) {
        super(layoutResId, data);
        this.activity = activity;
        this.rootView = rootView;
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

                    TextView tvMove = view.findViewById(R.id.tv_move);
                    tvMove.setOnClickListener(v12 -> {
                        popWindow.dissmiss();
                        showMoveNotoPopwindow(rootView);
                    });
                    return false;
                });
    }
    View lastView;
    private void showMoveNotoPopwindow(View rootView){
        //初始化为-1
        moveToIndex = -1;
        List<String> termList = Arrays.asList("2017-2018上学期","2017-2018上学期","2017-2018上学期","2017-2018上学期");
        TermAdapter termAdapter = new TermAdapter(R.layout.item_text_line, termList);
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_move_note,null);
        RecyclerView recyclerView = view.findViewById(R.id.rv_notebook);
        recyclerView.setAdapter(termAdapter);
        termAdapter.setOnItemClickListener((adapter, view1, position) -> {
            view1.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            moveToIndex = position;
            if(lastView!=null)lastView.setBackgroundColor(Color.WHITE);
            lastView = view1;
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                .setView(view)//显示的布局
                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                .setBgDarkAlpha(0.7f) // 控制亮度
                .create()//创建PopupWindow
                .showAtLocation(rootView, Gravity.CENTER,0, 0);//显示PopupWindow

        View btnMove = view.findViewById(R.id.btn_ok);
        View btnCancel = view.findViewById(R.id.btn_cancel);
        btnMove.setOnClickListener(v -> {
            popWindow.dissmiss();
            //Todo 移动
        });

        btnCancel.setOnClickListener(v -> popWindow.dissmiss());
    }

}
