package com.ace.qnote.activity;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ace.network.service.NoteService;
import com.ace.network.util.CallBack;
import com.ace.network.util.ConstUrl;
import com.ace.network.util.NetUtil;
import com.ace.network.util.RxReturnData;
import com.ace.qnote.R;
import com.ace.qnote.adapter.NoteContentAdapter;
import com.ace.qnote.adapter.NoteContentEditAdapter;
import com.ace.qnote.base.BaseActivity;
import com.ace.qnote.util.CommonUtils;
import com.ace.qnote.util.Const;
import com.ace.qnote.util.MD5Util;
import com.ace.qnote.util.oss.OssListener;
import com.ace.qnote.util.oss.OssUtil;
import com.ace.qnote.util.permission.ActionCallBackListener;
//import com.ace.qnote.util.permission.RxPermissionUtil;
import com.ace.qnote.util.permission.RxPermissionUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.example.zhouwei.library.CustomPopWindow;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.litepal.LitePal;
import org.litepal.crud.callback.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import csu.edu.ice.model.dao.NoteBean;
import csu.edu.ice.model.model.ContentBean;
import csu.edu.ice.model.model.SimpleContentBean;
import me.iwf.photopicker.PhotoPicker;

public class NoteContentActivity extends BaseActivity {
    private final int REQUEST_CODE_CHOOSE = 1000;

    private RecyclerView rvNoteContent;
    private NoteContentAdapter noteContentAdapter;
    private NoteContentEditAdapter noteContentEditAdapter;
    private OnItemDragListener onItemDragListener;
    private OnItemSwipeListener onItemSwipeListener;
    private ImageView ivAddText;
    private ImageView ivAddPic;
    private TextView tvTitle;
    private String[] m_upLoadImgPermission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private List<Uri> mSelected;
    private LinearLayout llShare;
    private LinearLayout llBack;
    private LinearLayout llAdd;
    private boolean isEditing = false;
    private String title = "";
    private String noteId = "";
    private Boolean needToScroll;
    private ArrayList<SimpleContentBean> contentList;
    private int curModifyItem;
    private NoteBean noteBean;
    private ContentBean contentBean;
    private int startMoveIndex;
    private int endMoveIndex;
    private Stack<String> contentStack;
    private LinearLayout llRevoke;

    @Override
    public void initParams(Bundle params) {
        if (params != null) {
            title = params.getString("title");
            noteId = params.getString("noteId");
            needToScroll = params.getBoolean("isAdd");
        }
        contentList = new ArrayList<>();
        contentStack = new Stack<>();
        noteBean = LitePal.where("noteId = ?", noteId).find(NoteBean.class).get(0);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_note_content;
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public void initView(View view) {
        rvNoteContent = findViewById(R.id.rv_note_content);
        ivAddPic = findViewById(R.id.iv_add_pic);
        ivAddText = findViewById(R.id.iv_add_text);
        tvTitle = findViewById(R.id.tv_title);
        llShare = findViewById(R.id.ll_share);
        llBack = findViewById(R.id.ll_back);
        llAdd = findViewById(R.id.ll_add);
        llRevoke = findViewById(R.id.ll_revoke);
    }

    @Override
    public void setListener() {
        ivAddPic.setOnClickListener(this);
        ivAddText.setOnClickListener(this);
        llBack.setOnClickListener(this);
        llShare.setOnClickListener(this);
        llRevoke.setOnClickListener(this);
        onItemDragListener = new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
                Vibrator vibrator = (Vibrator) getBaseContext().getSystemService(Service.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(500);
                }
                startMoveIndex =pos;
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {

            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
                if (contentBean!=null){
                    Collections.swap(contentBean.getBlocks(),startMoveIndex,pos);
                    Gson gson = new Gson();
                    noteBean.setContent(gson.toJson(contentBean));
                    saveNoteInfo(noteBean);
                }
            }
        };
        onItemSwipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {}
            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {}
            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                if (contentBean!=null){
                    contentBean.getBlocks().remove(pos);
                    Gson gson = new Gson();
                    noteBean.setContent(gson.toJson(contentBean));
                    saveNoteInfo(noteBean);
                }
            }
            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

            }
        };
        ivAddPic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                addPicFromFile();
                return false;
            }
        });
        rvNoteContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isEditing){
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            doFadeOut(llAdd);
                            break;
                        case RecyclerView.SCROLL_STATE_IDLE:
                            doFadeIn(llAdd);
                            break;
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void widgetClick(View v) throws Exception {
        switch (v.getId()) {
            case R.id.iv_add_pic:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    RxPermissionUtil.getInstance().requestPermission(this, new ActionCallBackListener() {
                        @Override
                        public void onSuccess(Object data) {
                            addPic();
                        }

                        @Override
                        public void onFailure(String errorEvent, String message) {
                            showToast("QNote似乎缺少了一些权限，无法上传照片，请到设置中授予权限再尝试");
                        }
                    }, m_upLoadImgPermission);
                } else {
                    addPic();
                }
                break;
            case R.id.ll_back:
                finish();
                break;
            case R.id.ll_share:
                Tencent mTencent = Tencent.createInstance("101488552", this);
                final Bundle params = new Bundle();
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, noteBean.getName());
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "来自好友分享的笔记");
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, ConstUrl.HOST+"#/share/"+noteId);
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://qnote-1253746866.cos.ap-guangzhou.myqcloud.com/logo.png");
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "企鹅笔记");
//                params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
                mTencent.shareToQQ(this, params, new IUiListener() {
                    @Override
                    public void onComplete(Object o) {

                    }

                    @Override
                    public void onError(UiError uiError) {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
                break;
            case R.id.iv_add_text:
                showModifyTextPopWindow("",true);
                break;
            case R.id.ll_revoke:
                if (!contentStack.isEmpty()){
                    String contentTemp = contentStack.pop();
                    noteBean.setContent(contentTemp);
                    parseContent(noteBean);
                }
                break;
        }
    }

    private void doFadeIn(View view) {
        view.setVisibility(View.INVISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setFillAfter(true);
        fadeIn.setDuration(500);
        view.startAnimation(fadeIn);
    }

    private void doFadeOut(View view) {
        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setFillAfter(true);
        fadeOut.setDuration(500);
        view.startAnimation(fadeOut);
        view.setVisibility(View.GONE);
    }

    private void addPicFromFile() {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(false)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    private void addPic() {
        Intent intent = new Intent(this, TakePhotoActivity.class);
        intent.putExtra("course_name", noteBean.getName());
        startActivityForResult(intent,REQUEST_CODE_CHOOSE);
    }

    @Override
    public void doBusiness(Context mContext) {
        if (!CommonUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

        initPageData();

        rvNoteContent.setLayoutManager(new LinearLayoutManager(this));
        noteContentAdapter = new NoteContentAdapter(R.layout.item_note_content, contentList);
        noteContentAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                rvNoteContent.post(() -> {
                    Vibrator vibrator = (Vibrator) getBaseContext().getSystemService(Service.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(500);
                    }
                    rvNoteContent.setAdapter(noteContentEditAdapter);
                    doFadeOut(llAdd);
//                    doFadeIn(famTools);
                    noteContentEditAdapter.enableSwipeItem();
                    noteContentEditAdapter.setOnItemSwipeListener(onItemSwipeListener);
                    rvNoteContent.scrollToPosition(position);
                    isEditing = true;
                });
                return false;
            }
        });

        noteContentEditAdapter = new NoteContentEditAdapter(R.layout.item_note_content_edit, contentList);
        noteContentEditAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.tv_text_edit:
                        curModifyItem = position;
                        showModifyTextPopWindow(((TextView)view).getText().toString(),false);
                        break;
                }
            }
        });

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(noteContentEditAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(rvNoteContent);
        noteContentEditAdapter.enableDragItem(itemTouchHelper, R.id.cv_content, true);
        noteContentEditAdapter.setOnItemDragListener(onItemDragListener);
        rvNoteContent.setAdapter(noteContentAdapter);
    }

    private void showModifyTextPopWindow(String text,Boolean isAdd) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_add_text,null);
        CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(view)//显示的布局
                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                .setBgDarkAlpha(0.7f) // 控制亮度
                .create()//创建PopupWindow
                .showAtLocation(getmContextView(), Gravity.CENTER,0, 0);//显示PopupWindow

        View btnOk = view.findViewById(R.id.btn_ok);
        View btnCancel = view.findViewById(R.id.btn_cancel);
        EditText editText = view.findViewById(R.id.et_text);
        editText.setText(text);
        btnOk.setOnClickListener(v -> {
            if (!CommonUtils.isEmpty(editText.getText().toString())){
                if (contentBean!=null){
                    ContentBean.BlocksBean blocksBean = new ContentBean().new BlocksBean();
                    blocksBean.setKey(MD5Util.crypt(UUID.randomUUID().toString()).substring(0,5));
                    blocksBean.setText(editText.getText().toString());
                    blocksBean.setType("unstyled");
                    blocksBean.setDepth(0);
                    blocksBean.setInlineStyleRanges(new ArrayList<>());
                    blocksBean.setEntityRanges(new ArrayList<>());
                    blocksBean.setData(new ContentBean().new BlocksBean().new DataBean());
                    if (isAdd){
                        contentBean.getBlocks().add(blocksBean);
                    }else {
                        contentBean.getBlocks().remove(curModifyItem);
                        contentBean.getBlocks().add(curModifyItem,blocksBean);
                    }
                    Gson gson = new Gson();
                    noteBean.setContent(gson.toJson(contentBean));
                    saveNoteInfo(noteBean);
                }
            }
            popWindow.dissmiss();
        });

        btnCancel.setOnClickListener(v -> popWindow.dissmiss());
    }

    private void initPageData() {
        NetUtil.doRetrofitRequest(NetUtil.getRetrofitInstance().create(NoteService.class).getNoteContent(noteId,Const.OPEN_ID), new CallBack<NoteBean>() {
            @Override
            public void onSuccess(NoteBean data) {
                parseContent(data);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void parseContent(NoteBean data) {
        String _tempJson = data.getContent();
        Gson gson = new Gson();
        contentBean = gson.fromJson(_tempJson,ContentBean.class);
        contentList.clear();
        for (ContentBean.BlocksBean blocksBean : contentBean.getBlocks()) {
            SimpleContentBean simpleContentBean = new SimpleContentBean();
            if (blocksBean.getEntityRanges().size()>0){
                String key = blocksBean.getEntityRanges().get(0).getKey()+"";
                simpleContentBean.setContent(contentBean.getEntityMap().get(key).getData().getUrl());
                simpleContentBean.setType(Const.CONTENT_TYPE_IMG);
            }else {
                simpleContentBean.setContent(blocksBean.getText());
                simpleContentBean.setType(Const.CONTENT_TYPE_TEXT);
            }
            contentList.add(simpleContentBean);
        }
        noteContentAdapter.notifyDataSetChanged();
//        if (needToScroll){
//            rvNoteContent.scrollToPosition(contentList.size()-1);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE||requestCode == REQUEST_CODE_CHOOSE)) {
            if (data != null) {
                ArrayList<String> photos = new ArrayList<>();
                if (requestCode == PhotoPicker.REQUEST_CODE) {
                    photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                }else if ( requestCode == REQUEST_CODE_CHOOSE){
                    photos = data.getStringArrayListExtra("picPath");
                }
                if (photos.size()>0) {
                    OssUtil.upload(OssUtil.getService(getBaseContext()), photos, new OssListener() {
                        @Override
                        public void onProgress(long progress, long max) {

                        }

                        @Override
                        public void onSuccess(ArrayList<String> url) {
                            savePhotos(url);
                        }

                        @Override
                        public void onFail() {

                        }
                    });
                }
            }
        }
    }

    private void savePhotos(ArrayList<String> photos) {
        int imgSize = 0;
        for (ContentBean.BlocksBean blocksBean : contentBean.getBlocks()) {
            if (blocksBean.getEntityRanges().size()>0){
                imgSize++;
            }
        }
        for (int i = 0; i < photos.size(); i++) {
            String picPath = photos.get(i);
            ContentBean.BlocksBean blocksBean = new ContentBean().new BlocksBean();
            blocksBean.setKey(MD5Util.crypt(UUID.randomUUID().toString()).substring(0,5));
            blocksBean.setDepth(0);
            blocksBean.setInlineStyleRanges(new ArrayList<>());
            blocksBean.setEntityRanges(new ArrayList<>());
            blocksBean.setData(new ContentBean().new BlocksBean().new DataBean());
            blocksBean.setText(" ");
            blocksBean.setType("atomic");
            ContentBean.BlocksBean.EntityRangesBean entityRangesBean = new ContentBean().new BlocksBean().new EntityRangesBean();
            entityRangesBean.setKey(imgSize+i+1);
            entityRangesBean.setLength(1);
            entityRangesBean.setOffset(0);
            blocksBean.getEntityRanges().add(entityRangesBean);
            contentBean.getBlocks().add(blocksBean);

            ContentBean.EntityBean entityBean = new ContentBean().new EntityBean();
            ContentBean.EntityBean.DataBean dataBean = new ContentBean().new EntityBean().new DataBean();
            dataBean.setMeta(new ContentBean().new EntityBean().new DataBean().new MetaBean());
            dataBean.setName(MD5Util.crypt(UUID.randomUUID().toString()));
            dataBean.setUrl(picPath);
            dataBean.setType("IMAGE");
            entityBean.setData(dataBean);
            entityBean.setMutability("IMMUTABLE");
            entityBean.setType("IMAGE");
            contentBean.getEntityMap().put((imgSize+i+1)+"",entityBean);
        }
        Gson gson = new Gson();
        noteBean.setContent(gson.toJson(contentBean));
        saveNoteInfo(noteBean);
    }

    @Override
    public void onBackPressed() {
        if (isEditing){
            rvNoteContent.setAdapter(noteContentAdapter);
            isEditing = false;
//            doFadeOut(famTools);
            doFadeIn(llAdd);
            noteContentEditAdapter.disableSwipeItem();
        }else {
            super.onBackPressed();
        }
    }

    private void saveNoteInfo(NoteBean noteBean) {
        noteBean.update(noteBean.get_id());
        NetUtil.doRetrofitRequest(NetUtil.noteService.update(Const.OPEN_ID, noteBean.getNoteId(),
                noteBean.getName(), noteBean.getBookRef(), noteBean.getIsKeyNote(), 0, true, noteBean.getContent()), new CallBack<RxReturnData>() {
            @Override
            public void onSuccess(RxReturnData data) {
                noteBean.setIsLocal(0);
                noteBean.setSyncTime(System.currentTimeMillis() / 1000 + "");
                noteBean.setRecentTime(System.currentTimeMillis() / 1000 + "");
                noteBean.update(noteBean.get_id());
                parseContent(noteBean);
                showToast("保存成功");
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onFailure(String message) {

            }
        });
//        noteBean.saveAsync().listen(new SaveCallback() {
//            @Override
//            public void onFinish(boolean success) {
//            }
//        });
//        NetUtil.doRetrofitRequest(NetUtil.noteService.addNote(Const.OPEN_ID, noteBean.getBookRef(),
//                noteBean.getName(), noteBean.getContent(), noteBean.getIsKeyNote()), new CallBack<RxReturnData>() {
//            @Override
//            public void onSuccess(RxReturnData data) {
//                noteBean.setIsLocal(0);
//                noteBean.setSyncTime(System.currentTimeMillis()/1000+"");
//                noteBean.update(noteBean.get_id());
//                noteBean.setRecentTime(System.currentTimeMillis()/1000+"");
//                parseContent(noteBean);
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//            }
//
//            @Override
//            public void onFailure(String message) {
//
//            }
//        });
//    }
    }
}
