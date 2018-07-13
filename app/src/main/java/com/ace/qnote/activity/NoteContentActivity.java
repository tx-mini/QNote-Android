package com.ace.qnote.activity;

import android.Manifest;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ace.qnote.R;
import com.ace.qnote.adapter.NoteContentAdapter;
import com.ace.qnote.adapter.NoteContentEditAdapter;
import com.ace.qnote.base.BaseActivity;
import com.ace.qnote.util.CommonUtils;
import com.ace.qnote.util.Const;
import com.ace.qnote.util.permission.ActionCallBackListener;
//import com.ace.qnote.util.permission.RxPermissionUtil;
import com.ace.qnote.util.permission.RxPermissionUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;
import java.util.List;

import csu.edu.ice.model.ContentBean;
import csu.edu.ice.model.SimpleContentBean;
import me.iwf.photopicker.PhotoPicker;

public class NoteContentActivity extends BaseActivity {
    private final int REQUEST_CODE_CHOOSE = 1000;

    private RecyclerView rv_note_content;
    private NoteContentAdapter noteContentAdapter;
    private NoteContentEditAdapter noteContentEditAdapter;
    private OnItemDragListener onItemDragListener;
    private OnItemSwipeListener onItemSwipeListener;
    private ImageView iv_add_text;
    private ImageView iv_add_pic;
    private TextView tv_title;
    private String[] m_upLoadImgPermission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private List<Uri> mSelected;
    private LinearLayout ll_share;
    private LinearLayout ll_back;
    private LinearLayout ll_add;
    private FloatingActionMenu fam_tools;
    private boolean isEditing = false;
    private String title = "";
    private ArrayList<SimpleContentBean> contentList;

    @Override
    public void initParams(Bundle params) {
        if (params != null) {
            title = params.getString("title");
        }
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
        rv_note_content = findViewById(R.id.rv_note_content);
        iv_add_pic = findViewById(R.id.iv_add_pic);
        iv_add_text = findViewById(R.id.iv_add_text);
        tv_title = findViewById(R.id.tv_title);
        ll_share = findViewById(R.id.ll_share);
        ll_back = findViewById(R.id.ll_back);
        ll_add = findViewById(R.id.ll_add);
        iv_add_text = findViewById(R.id.iv_add_text);
        fam_tools = findViewById(R.id.fam_tools);
    }

    @Override
    public void setListener() {
        iv_add_pic.setOnClickListener(this);
        iv_add_text.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        ll_share.setOnClickListener(this);
        onItemDragListener = new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
                Vibrator vibrator = (Vibrator) getBaseContext().getSystemService(Service.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(500);
                }
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {

            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {

            }
        };
        onItemSwipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {}
            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {}
            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            }
            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

            }
        };
        iv_add_pic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                addPicFromFile();
                return false;
            }
        });
        rv_note_content.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isEditing){
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            doFadeOut(ll_add);
                            break;
                        case RecyclerView.SCROLL_STATE_IDLE:
                            doFadeIn(ll_add);
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
                params.putString(QQShare.SHARE_TO_QQ_TITLE, "要分享的标题");
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "要分享的摘要");
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.qq.com/news/1.html");
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "测试应用222222");
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
                showAddTextDialog();
        }
    }

    private void showAddTextDialog() {
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
        intent.putExtra("course_name", "软件工程");
        startActivity(intent);
    }

    @Override
    public void doBusiness(Context mContext) {
        if (!CommonUtils.isEmpty(title)) {
            tv_title.setText(title);
        }

        initPageData();

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("http://img3.imgtn.bdimg.com/it/u=80472546,670250703&fm=27&gp=0.jpg");
        arrayList.add("这节课都讲的什么东西");
        arrayList.add("http://img0.imgtn.bdimg.com/it/u=2796201933,4190184343&fm=27&gp=0.jpg");
        arrayList.add("TCP三次握手四次挥手");
        arrayList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1531574283&di=d4d7d2226260b741c657916e7f014825&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01fc245798c5220000018c1b3264ef.jpg%401280w_1l_2o_100sh.png");

        rv_note_content.setLayoutManager(new LinearLayoutManager(this));
        noteContentAdapter = new NoteContentAdapter(R.layout.item_note_content, contentList);
        noteContentEditAdapter = new NoteContentEditAdapter(R.layout.item_note_content_edit, contentList);
        noteContentAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                rv_note_content.post(() -> {
                    Vibrator vibrator = (Vibrator) getBaseContext().getSystemService(Service.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(500);
                    }
                    rv_note_content.setAdapter(noteContentEditAdapter);
                    doFadeOut(ll_add);
                    doFadeIn(fam_tools);
                    noteContentEditAdapter.enableSwipeItem();
                    noteContentEditAdapter.setOnItemSwipeListener(onItemSwipeListener);
                    isEditing = true;
                });
                return false;
            }
        });

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(noteContentEditAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(rv_note_content);
        noteContentEditAdapter.enableDragItem(itemTouchHelper, R.id.cv_content, true);
        noteContentEditAdapter.setOnItemDragListener(onItemDragListener);
        rv_note_content.setAdapter(noteContentAdapter);
    }

    private void initPageData() {
        contentList = new ArrayList<>();
        String _tempJson =
                "{\"blocks\":[{\"key\":\"d5l3p\",\"text\":\"123asdasdasdas23d1as321d856qw4e65wq41651d53as1d8qw4r56qw4f5631asf56a4sf654as5f6a4s56f4as65465\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}},{\"key\":\"9f3dc\",\"text\":\"\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}},{\"key\":\"3vq85\",\"text\":\"xxxx\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}},{\"key\":\"fgknl\",\"text\":\"\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}},{\"key\":\"uhbi\",\"text\":\"\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}},{\"key\":\"dk450\",\"text\":\"\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}},{\"key\":\"13li4\",\"text\":\"\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}},{\"key\":\"3cjrf\",\"text\":\" \",\"type\":\"atomic\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[{\"offset\":0,\"length\":1,\"key\":0}],\"data\":{}},{\"key\":\"e783e\",\"text\":\" \",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}},{\"key\":\"6dsjs\",\"text\":\"\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}},{\"key\":\"6g1l5\",\"text\":\"dsadasd\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}},{\"key\":\"a2oet\",\"text\":\" \",\"type\":\"atomic\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[{\"offset\":0,\"length\":1,\"key\":1}],\"data\":{}},{\"key\":\"dpbis\",\"text\":\"\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}}],\"entityMap\":{\"0\":{\"type\":\"IMAGE\",\"mutability\":\"IMMUTABLE\",\"data\":{\"url\":\"https://cdn.flutterchina.club/images/flutter-mark-square-100.png\",\"name\":\"QQ20180526-104511@2x.png\",\"type\":\"IMAGE\",\"meta\":{}}},\"1\":{\"type\":\"IMAGE\",\"mutability\":\"IMMUTABLE\",\"data\":{\"url\":\"https://cdn.flutterchina.club/images/flutter-mark-square-100.png\",\"name\":\"QQ20180526-104511@2x.png\",\"type\":\"IMAGE\",\"meta\":{}}}}}";
        Gson gson = new Gson();
        ContentBean contentBean = gson.fromJson(_tempJson,ContentBean.class);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isEditing){
            rv_note_content.setAdapter(noteContentAdapter);
            isEditing = false;
            doFadeOut(fam_tools);
            doFadeIn(ll_add);
            noteContentEditAdapter.disableSwipeItem();
        }else {
            super.onBackPressed();
        }
    }
}
