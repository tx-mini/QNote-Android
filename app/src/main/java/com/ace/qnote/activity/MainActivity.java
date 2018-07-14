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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ace.network.service.NoteService;
import com.ace.network.util.CallBack;
import com.ace.network.util.NetUtil;
import com.ace.network.util.RxReturnData;
import com.ace.qnote.R;
import com.ace.qnote.adapter.DrawerNoteAdapter;
import com.ace.qnote.adapter.NoteAdapter;
import com.ace.qnote.adapter.TermAdapter;
import com.ace.qnote.base.BaseActivity;
import com.ace.qnote.util.Const;
import com.ace.qnote.util.permission.ActionCallBackListener;
import com.ace.qnote.util.permission.RxPermissionUtil;
import com.example.zhouwei.library.CustomPopWindow;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import csu.edu.ice.model.dao.BookBean;
import csu.edu.ice.model.dao.NoteBean;
import csu.edu.ice.model.dao.TermBean;
import csu.edu.ice.model.model.TermResult;
import me.iwf.photopicker.PhotoPicker;

public class MainActivity extends BaseActivity {
    private final int REQUEST_CODE_CHOOSE = 1000;
    private ImageView ivPic,ivEdit,ivTakePhoto,ivDeleteNoteBook,ivAddNote;
    private TextView tvNickname;
    private TextView tvTerm;
    private TextView tvYear;
    private LinearLayout layoutTerm;
    private LinearLayout layoutArchive,layoutNewNote, layoutCourseTable,layoutDustbin,layoutStudy;
    private RecyclerView rvNotebook,rvNote;
    private String[] m_upLoadImgPermission =  {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private List<Uri> mSelected;
    private List<BookBean> notebookList;
    private List<TermBean> termList;
    private View rootView;
    private DrawerNoteAdapter drawerNoteAdapter;
    private NoteAdapter noteAdapter;
    private ArrayList noteList;
    private BookBean notebook;

    @Override
    public void initParams(Bundle params) {
        notebookList = new ArrayList<>();
        termList = new ArrayList<>();
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
        ivDeleteNoteBook = findViewById(R.id.iv_delete_book);
        ivAddNote = findViewById(R.id.iv_add_note);
        tvYear = findViewById(R.id.tv_year);
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
        ivDeleteNoteBook.setOnClickListener(this);
        ivAddNote.setOnClickListener(this);
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

            case R.id.iv_delete_book:
                showDeleteNoteBookPopwindow();
                break;
            case R.id.iv_add_note:
                showAddNotePopwindow();
                break;
        }
    }

    private void showDeleteNoteBookPopwindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_delete_note,null);
        TextView tvMessage = view.findViewById(R.id.tv_message);
        CustomPopWindow popWindow = showWindow(view);
        view.findViewById(R.id.btn_cancel).setOnClickListener(v-> popWindow.dissmiss());
        view.findViewById(R.id.btn_ok).setOnClickListener(v -> {
            //删除笔记本
            popWindow.dissmiss();
        });
    }

    private CustomPopWindow showWindow(View view){
        return  new CustomPopWindow.PopupWindowBuilder(this)
                .setView(view)//显示的布局
                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                .setBgDarkAlpha(0.7f) // 控制亮度
                .create()//创建PopupWindow
                .showAtLocation(getmContextView(), Gravity.CENTER,0, 0);//显示PopupWindow
    }

    private void showAddNotePopwindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_rename,null);
        EditText etName = view.findViewById(R.id.et_name);
        etName.setHint("请输入笔记名称");
        CustomPopWindow popWindow = showWindow(view);
        view.findViewById(R.id.btn_ok).setOnClickListener(v->{
            showToast("添加笔记");
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(v->{
            popWindow.dissmiss();
        });
    }

    private void showChooseTermPopwindow() {
        TermAdapter termAdapter = new TermAdapter(R.layout.item_text_line, termList);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_term,null);
        RecyclerView recyclerView = view.findViewById(R.id.rv_term);
        recyclerView.setAdapter(termAdapter);
        termAdapter.setOnItemChildClickListener((adapter, view1, position) -> {

        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CustomPopWindow popWindow = showWindow(view);

    }

    private void addPic() {
        Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
        intent.putExtra("course_name","软件工程");
        startActivity(intent);
    }

    @Override
    public void doBusiness(Context mContext) {
        syncDataFromNet();
        noteAdapter = new NoteAdapter(R.layout.item_note,noteList,this,notebook,rootView);
        noteAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", (String) adapter.getData().get(position));
            startActivity(NoteContentActivity.class,bundle);
        });
        rvNote.setAdapter(noteAdapter);
        rvNote.setLayoutManager(new LinearLayoutManager(this));

        //侧滑栏recycleView初始化
        getDataFromLocal();
        drawerNoteAdapter = new DrawerNoteAdapter(R.layout.item_drawer_note,notebookList);
        rvNotebook.setAdapter(drawerNoteAdapter);
        rvNotebook.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showNoteList(){

        noteList = new ArrayList<>();
        NetUtil.doRetrofitRequest(NetUtil.getRetrofitInstance().create(NoteService.class).getNoteList("1"), new CallBack<RxReturnData<List<NoteBean>>>() {
            @Override
            public void onSuccess(RxReturnData<List<NoteBean>> data) {
                if(data.getCode() == 200){
                    noteList.clear();
                    noteList.addAll(data.getResult());
                    noteAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void getDataFromLocal() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
        thread.run();
    }

    private void syncDataFromNet() {
        NetUtil.doRetrofitRequest(NetUtil.getRetrofitInstance().create(NoteService.class).getTermAndRubbish(Const.OPEN_ID), new CallBack<TermResult>() {
            @Override
            public void onSuccess(TermResult data) {
                notebookList.clear();
                notebookList.addAll(data.getTermList().get(0).getChildren());
                for (TermBean termBean : data.getTermList()) {
                    for (BookBean bookBean : termBean.getChildren()) {
                        bookBean.setTerm(termBean.getTerm());
                    }
                    notebookList.addAll(termBean.getChildren());
                }

                termList.clear();
                termList.addAll(data.getTermList());

                LitePal.saveAll(notebookList);
                LitePal.saveAll(termList);
                drawerNoteAdapter.notifyDataSetChanged();
                if(notebookList!=null && notebookList.size()>0) {
                    notebook = notebookList.get(0);
                }
//                notebookList.addAll()

                //显示最新的学期
                tvYear.setText(termList.get(termList.size()-1).getTerm());
                showNoteList();
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
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
