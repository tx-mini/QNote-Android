package com.ace.qnote.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ace.qnote.R;
import com.ace.qnote.adapter.DrawerNoteAdapter;
import com.ace.qnote.adapter.NoteAdapter;
import com.ace.qnote.adapter.TermAdapter;
import com.ace.qnote.base.BaseActivity;
import com.ace.qnote.util.CommonUtils;
import com.ace.qnote.util.permission.ActionCallBackListener;
import com.ace.qnote.util.permission.RxPermissionUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zhouwei.library.CustomPopWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;

public class MainActivity extends BaseActivity {
    private final int REQUEST_CODE_CHOOSE = 1000;
    private ImageView ivPic,ivEdit,ivTakePhoto;
    private TextView tvNickname;
    private TextView tvTerm;
    private LinearLayout layoutTerm;
    private LinearLayout layoutArchive,layoutNewNote, layoutCourseTable,layoutDustbin,layoutStudy;
    private RecyclerView rvNotebook,rvNote;
    private String[] m_upLoadImgPermission =  {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private List<Uri> mSelected;
    private View rootView;

    @Override
    public void initParams(Bundle params) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public void initView(View view) {
        rootView = view;
        ivPic = findViewById(R.id.iv_pic);
        tvNickname = findViewById(R.id.tv_nickname);
        tvTerm = findViewById(R.id.tv_term);
        layoutTerm = findViewById(R.id.layout_term);
        layoutDustbin = findViewById(R.id.layout_dustbin);
        layoutStudy = findViewById(R.id.layout_study);
        layoutArchive = findViewById(R.id.layout_archive);
        layoutNewNote = findViewById(R.id.layout_new_notebook);
        layoutCourseTable = findViewById(R.id.layout_course_table);
        rvNotebook = findViewById(R.id.rv_notebook);
        ivEdit = findViewById(R.id.iv_edit);
        ivTakePhoto = findViewById(R.id.iv_take_photo);
        rvNote = findViewById(R.id.rv_note);
    }

    @Override
    public void setListener() {
        ivPic.setOnClickListener(this);
        tvNickname.setOnClickListener(this);
        tvTerm.setOnClickListener(this);
        layoutTerm.setOnClickListener(this);
        layoutDustbin.setOnClickListener(this);
        layoutStudy.setOnClickListener(this);
        layoutArchive.setOnClickListener(this);
        layoutNewNote.setOnClickListener(this);
        layoutCourseTable.setOnClickListener(this);
        ivEdit.setOnClickListener(this);
        ivTakePhoto.setOnClickListener(this);
        ivTakePhoto.setOnLongClickListener(v -> {
            addPicFromFile();
            return false;
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
            case R.id.tv_term:
                showChooseTermPopwindow();
                break;
            case R.id.layout_archive:
                break;
            case R.id.layout_course_table:
                startActivity(new Intent(this,CourseActivity.class));
                break;
            case R.id.layout_dustbin:
                break;
            case R.id.layout_new_notebook:
                break;
            case R.id.layout_study:
                break;

            case R.id.tv_nickname:
            case R.id.iv_pic:
                startActivity(new Intent(this,InformationActivity.class));
                break;
            case R.id.iv_edit:

                break;
            case R.id.iv_take_photo:
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
        }
    }

    private void showChooseTermPopwindow() {
        List<String> termList = Arrays.asList("2017-2018上学期","2017-2018上学期","2017-2018上学期","2017-2018上学期");
        TermAdapter termAdapter = new TermAdapter(R.layout.item_text_line, termList);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_term,null);
        RecyclerView recyclerView = view.findViewById(R.id.rv_term);
        recyclerView.setAdapter(termAdapter);
        termAdapter.setOnItemChildClickListener((adapter, view1, position) -> {

        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int margin = CommonUtils.dip2px(this,10)*2;
        int width = CommonUtils.dip2px(this,260)+margin;
        CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(view)//显示的布局
                .size(width, (int) (getResources().getDimension(R.dimen.item_term_height)*termList.size()+margin))
                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                .setBgDarkAlpha(0.7f) // 控制亮度
                .create()//创建PopupWindow
                .showAtLocation(getmContextView(), Gravity.CENTER,0, 0);//显示PopupWindow
    }



    private void addPic() {
        Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
        intent.putExtra("course_name","软件工程");
        startActivity(intent);
    }

    @Override
    public void doBusiness(Context mContext) {
        List<String> notebookList = Arrays.asList("大型数据库","云计算及应用","软件过程与改进");
        DrawerNoteAdapter adapter = new DrawerNoteAdapter(R.layout.item_drawer_note,notebookList);
        rvNotebook.setAdapter(adapter);
        rvNotebook.setLayoutManager(new LinearLayoutManager(this));

        List<String> noteList = Arrays.asList("2018-7-6笔记","2018-7-8笔记","2018-7-10笔记","2018-7-16笔记");
        NoteAdapter noteAdapter = new NoteAdapter(R.layout.item_note,noteList,this,rootView);
        noteAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("title", (String) adapter.getData().get(position));
                startActivity(NoteContentActivity.class,bundle);
            }
        });
        rvNote.setAdapter(noteAdapter);
        rvNote.setLayoutManager(new LinearLayoutManager(this));
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
