package com.ace.qnote.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ace.qnote.R;
import com.ace.qnote.adapter.NoteContentAdapter;
import com.ace.qnote.base.BaseActivity;
import com.ace.qnote.util.CommonUtils;
import com.ace.qnote.util.permission.ActionCallBackListener;
//import com.ace.qnote.util.permission.RxPermissionUtil;
import com.ace.qnote.util.permission.RxPermissionUtil;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;

public class NoteContentActivity extends BaseActivity{
    private final int REQUEST_CODE_CHOOSE = 1000;

    private RecyclerView rv_note_content;
    private NoteContentAdapter noteContentAdapter;
    private OnItemDragListener onItemDragListener;
    private ImageView iv_add_text;
    private ImageView iv_add_pic;
    private TextView tv_title;
    private String[] m_upLoadImgPermission =  {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private List<Uri> mSelected;
    private String title="";

    @Override
    public void initParams(Bundle params) {
        if (params!=null){
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
    }

    @Override
    public void setListener() {
        iv_add_pic.setOnClickListener(this);
        iv_add_text.setOnClickListener(this);
        onItemDragListener = new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {

            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {

            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {

            }
        };
        iv_add_pic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                addPicFromFile();
                return false;
            }
        });
    }

    private void addPicFromFile() {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(false)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    @Override
    public void widgetClick(View v) throws Exception {
        switch (v.getId()){
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
                }else {
                    addPic();
                }
                break;
            case R.id.iv_add_text:
                break;
        }
    }

    private void addPic() {
        Intent intent = new Intent(this, TakePhotoActivity.class);
        intent.putExtra("course_name","软件工程");
        startActivity(intent);
    }

    @Override
    public void doBusiness(Context mContext) {
        if (!CommonUtils.isEmpty(title)){
            tv_title.setText(title);
        }
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("http://img3.imgtn.bdimg.com/it/u=80472546,670250703&fm=27&gp=0.jpg");
        arrayList.add("这节课都讲的什么东西");
        arrayList.add("http://img0.imgtn.bdimg.com/it/u=2796201933,4190184343&fm=27&gp=0.jpg");
        arrayList.add("TCP三次握手四次挥手");
        arrayList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1531574283&di=d4d7d2226260b741c657916e7f014825&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01fc245798c5220000018c1b3264ef.jpg%401280w_1l_2o_100sh.png");

        rv_note_content.setLayoutManager(new LinearLayoutManager(this));
        noteContentAdapter = new NoteContentAdapter(R.layout.item_note_content,arrayList);
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(noteContentAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(rv_note_content);
        noteContentAdapter.enableDragItem(itemTouchHelper, R.id.ll_content, true);
        noteContentAdapter.setOnItemDragListener(onItemDragListener);

        rv_note_content.setAdapter(noteContentAdapter);
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
}
