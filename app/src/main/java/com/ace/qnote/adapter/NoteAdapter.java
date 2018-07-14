package com.ace.qnote.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ace.network.util.CallBack;
import com.ace.network.util.NetUtil;
import com.ace.qnote.R;
import com.ace.qnote.util.CommonUtils;
import com.ace.qnote.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zhouwei.library.CustomPopWindow;

import org.litepal.LitePal;

import java.util.List;

import csu.edu.ice.model.dao.BookBean;
import csu.edu.ice.model.dao.NoteBean;

/**
 * Created by ice on 2018/7/10.
 */

public class NoteAdapter extends BaseQuickAdapter<NoteBean, BaseViewHolder> {
    Activity activity;
    View rootView;
    private int moveToIndex;
    private int curPosition;
    private BookBean noteBook;

    public NoteAdapter(int layoutResId, @Nullable List<NoteBean> data, Activity activity, BookBean notebook, View rootView) {
        super(layoutResId, data);
        this.activity = activity;
        this.rootView = rootView;
        this.noteBook = notebook;
    }

    @Override
    protected void convert(BaseViewHolder helper, NoteBean item) {
        helper.setText(R.id.tv_note_title, item.getName());
        if (item.getRecentTime() != null) {
            helper.setText(R.id.tv_date, item.getRecentTime().toString());
        }

        if (item.getIsRubbish() == 0) {
            showNormalNotePop(helper, item);
        } else {
            showRubbishNotePop(helper, item);
        }


        curPosition = helper.getAdapterPosition();
    }

    private void showRubbishNotePop(BaseViewHolder helper, NoteBean item) {
        helper.getView(R.id.layout_root)
                .setOnLongClickListener(v -> {
                    //弹出菜单
                    View menuView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_menu, null);
                    TextView tvDelete = menuView.findViewById(R.id.tv_delete);
                    TextView tvTitle = menuView.findViewById(R.id.tv_title);
                    tvTitle.setText(item.getName());

                    int xOffset = (int) (CommonUtils.getScreenWidth(activity) * 0.45);
                    CustomPopWindow popMenuWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                            .setView(menuView)
                            .create()
                            .showAsDropDown(v, xOffset, -CommonUtils.dip2px(mContext, 25), Gravity.CENTER);

                    //删除
                    tvDelete.setOnClickListener(v1 -> {
                        popMenuWindow.dissmiss();
                        showDeletePopwindow(rootView, item);
                    });

                    //移动
                    TextView tvMove = menuView.findViewById(R.id.tv_move);
                    tvMove.setOnClickListener(v12 -> {
                        popMenuWindow.dissmiss();
                        showMoveNotePopwindow(rootView, item);
                    });

                    //重命名
                    TextView tvRename = menuView.findViewById(R.id.tv_rename);
                    tvRename.setOnClickListener(v12 -> {
                        popMenuWindow.dissmiss();
                        showRenameNotePopwindow(rootView, item);
                    });
                    return false;
                });
    }


    private void showNormalNotePop(BaseViewHolder helper, NoteBean item) {
        helper.getView(R.id.layout_root)
                .setOnLongClickListener(v -> {
                    //弹出菜单
                    View menuView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_menu_rubbish, null);
                    TextView tvTitle = menuView.findViewById(R.id.tv_title);
                    tvTitle.setText(item.getName());

                    int xOffset = (int) (CommonUtils.getScreenWidth(activity) * 0.45);
                    CustomPopWindow popMenuWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                            .setView(menuView)
                            .create()
                            .showAsDropDown(v, xOffset, -CommonUtils.dip2px(mContext, 25), Gravity.CENTER);

                    TextView tvDelete = menuView.findViewById(R.id.tv_delete);
                    //删除
                    tvDelete.setOnClickListener(v1 -> {
                        popMenuWindow.dissmiss();
                        showDeletePopwindow(rootView, item);
                    });

                    //重命名
                    TextView tvRename = menuView.findViewById(R.id.tv_restore);
                    tvRename.setOnClickListener(v12 -> {
                        popMenuWindow.dissmiss();
                        showRenameNotePopwindow(rootView, item);
                    });

                    return false;
                });
    }


    private void showDeletePopwindow(View rootView, NoteBean item) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_delete_note,null);
        if(item.getIsRubbish()==1) {
            ((TextView) view.findViewById(R.id.tv_message)).setText("确定要删除「"+item.getName()+"」吗？");
        }else{
            ((TextView) view.findViewById(R.id.tv_message)).setText("确定要将「"+item.getName()+"」移入垃圾桶么？");
        }
        CustomPopWindow popDeleteWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                .setView(view)
                .enableBackgroundDark(true)
                .setBgDarkAlpha(0.7f)
                .create()
                .showAtLocation(rootView, Gravity.CENTER,0,0);
        view.findViewById(R.id.btn_cancel).setOnClickListener(v->popDeleteWindow.dissmiss());
        view.findViewById(R.id.btn_ok).setOnClickListener(v->{
            //确定删除
            if(item.getIsRubbish()==1){
                //已经是垃圾桶里面的了 直接删除
                NetUtil.doRetrofitRequest(NetUtil.noteService.deleteNote(Const.OPEN_ID, item.getId()), new CallBack<String>() {
                    @Override
                    public void onSuccess(String data) {
                        getData().remove(item);
                        notifyDataSetChanged();
                        Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onFailure(String message) {

                    }
                });
            }else{
                //移动到垃圾桶
/*                NetUtil.doRetrofitRequest(NetUtil.noteService.update(), new CallBack<String>() {
                    @Override
                    public void onSuccess(String data) {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onFailure(String message) {

                    }
                });*/
            }
        });
    }

    View lastView;

    private void showMoveNotePopwindow(View rootView, NoteBean noteBean) {
        //初始化为-1
        moveToIndex = -1;
        List<BookBean> bookList = LitePal.where("term = ?", noteBook.getTerm() + "").find(BookBean.class);
        DrawerNoteAdapter termAdapter = new DrawerNoteAdapter(R.layout.item_text_line, bookList);
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_move_note, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(noteBook.getTerm());
        RecyclerView recyclerView = view.findViewById(R.id.rv_notebook);
        recyclerView.setAdapter(termAdapter);
        termAdapter.setOnItemClickListener((adapter, view1, position) -> {
            view1.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            view1.findViewById(R.id.line).setVisibility(View.INVISIBLE);
            moveToIndex = position;
            if (lastView != null) {
                lastView.setBackgroundColor(Color.WHITE);
                lastView.findViewById(R.id.line).setVisibility(View.VISIBLE);
            }
            lastView = view1;
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                .setView(view)//显示的布局
                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                .setBgDarkAlpha(0.7f) // 控制亮度
                .create()//创建PopupWindow
                .showAtLocation(rootView, Gravity.CENTER, 0, 0);//显示PopupWindow

        View btnMove = view.findViewById(R.id.btn_ok);
        View btnCancel = view.findViewById(R.id.btn_cancel);
        btnMove.setOnClickListener(v -> {
            popWindow.dissmiss();
            //Todo 移动
            NetUtil.doRetrofitRequest(NetUtil.noteService.move(noteBean.getId(), noteBook.getId()), new CallBack<String>() {
                @Override
                public void onSuccess(String data) {

                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onFailure(String message) {

                }
            });
        });

        btnCancel.setOnClickListener(v -> popWindow.dissmiss());
    }

    private void showRenameNotePopwindow(View rootView, NoteBean item) {
        //初始化为-1
        moveToIndex = -1;
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_rename, null);
        CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                .setView(view)//显示的布局
                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                .setBgDarkAlpha(0.7f) // 控制亮度
                .create()//创建PopupWindow
                .showAtLocation(rootView, Gravity.CENTER, 0, 0);//显示PopupWindow

        View btnOk = view.findViewById(R.id.btn_ok);
        View btnCancel = view.findViewById(R.id.btn_cancel);
        EditText editText = view.findViewById(R.id.et_name);
        editText.setText(getData().get(curPosition).getName());
        btnOk.setOnClickListener(v -> {
            String newTitle = editText.getText().toString();
            if (!CommonUtils.isEmpty(newTitle)) {

                NetUtil.doRetrofitRequest(NetUtil.noteService.rename(item.getId(), newTitle), new CallBack<String>() {
                    @Override
                    public void onSuccess(String data) {
                        //刷新界面
                        item.setName(newTitle);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onFailure(String message) {

                    }
                });

            }
            popWindow.dissmiss();
        });

        btnCancel.setOnClickListener(v -> popWindow.dissmiss());
    }

}
