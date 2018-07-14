package com.ace.qnote.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ace.network.service.NoteService;
import com.ace.network.util.CallBack;
import com.ace.network.util.NetUtil;
import com.ace.qnote.R;
import com.ace.qnote.adapter.DrawerNoteAdapter;
import com.ace.qnote.adapter.NoteAdapter;
import com.ace.qnote.adapter.TermAdapter;
import com.ace.qnote.base.BaseActivity;
import com.ace.qnote.util.Const;
import com.ace.qnote.util.permission.ActionCallBackListener;
import com.ace.qnote.util.permission.RxPermissionUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
    private ImageView ivEdit,ivTakePhoto;
    private TextView tvNickname;
    private TextView tvTerm;
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
    private ArrayList<NoteBean> noteList;
    private BookBean notebook;
    private TextView tvName;

    private RelativeLayout layoutHide;
    private RelativeLayout layoutNormal;
    private int term;

    @Override
    public void initParams(Bundle params) {
        notebookList = new ArrayList<>();
        termList = new ArrayList<>();
        noteList = new ArrayList<>();
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
        tvName = findViewById(R.id.tv_name);
    }

    @Override
    public void setListener() {
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
                openDustbin();
                break;
            case R.id.layout_new_notebook:
                showAddNotePopwindow(term);
                break;
            case R.id.layout_study:
                break;

            case R.id.tv_nickname:
            case R.id.iv_image:
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

    private void openDustbin() {
        NetUtil.doRetrofitRequest(NetUtil.getRetrofitInstance().create(NoteService.class).getNoteList(Const.OPEN_ID,"",1,0), new CallBack<List<NoteBean>>() {
            @Override
            public void onSuccess(List<NoteBean> data) {
                noteList.clear();
                noteList.addAll(data);
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
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

    private void showAddNotePopwindow(int term) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_rename,null);
        EditText etName = view.findViewById(R.id.et_name);
        etName.setHint("请输入笔记名称");
        CustomPopWindow popWindow = showWindow(view);
        view.findViewById(R.id.btn_ok).setOnClickListener(v->{

            if(TextUtils.isEmpty(etName.getText().toString())){
                Toast.makeText(this, "请输入笔记本名称！", Toast.LENGTH_SHORT).show();
                return;
            }
            NetUtil.doRetrofitRequest(NetUtil.noteService.addBook(Const.OPEN_ID, term, etName.getText().toString()), new CallBack<BookBean>() {
                @Override
                public void onSuccess(BookBean data) {
                    data.save();
                    notebookList.add(data);
                    drawerNoteAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onFailure(String message) {

                }
            });

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

        Glide.with(this).load(getSharedPreferences(Const.SP_NAME,MODE_PRIVATE).getString("imageUrl",""))
                .apply(new RequestOptions().circleCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher))
                .into((ImageView) findViewById(R.id.iv_image));

        ((TextView)findViewById(R.id.tv_nickname)).setText(getSharedPreferences(Const.SP_NAME,MODE_PRIVATE).getString("nickname","加载失败"));


        initNoteRecyclerView();
        initDrawerRecyclerView();
        if(NetUtil.isNetworkConnected(this)){
            //有网络 从服务器请求
            syncDataFromNet();
        }else {
            getDataFromLocal();
        }

    }
    private void initNoteRecyclerView() {
        noteAdapter = new NoteAdapter(R.layout.item_note,noteList,this,notebook,rootView);
        noteAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", (String) adapter.getData().get(position));
            startActivity(NoteContentActivity.class,bundle);
        });
        rvNote.setAdapter(noteAdapter);
        rvNote.setLayoutManager(new LinearLayoutManager(this));

    }
    private void initDrawerRecyclerView() {
        drawerNoteAdapter = new DrawerNoteAdapter(R.layout.item_drawer_note,notebookList);
        rvNotebook.setAdapter(drawerNoteAdapter);
        rvNotebook.setLayoutManager(new LinearLayoutManager(this));
    }



    private void showNoteList(String book_id){

        noteList = new ArrayList<>();
        NetUtil.doRetrofitRequest(NetUtil.getRetrofitInstance().create(NoteService.class).getNoteList("1"), new CallBack<List<NoteBean>>() {
            @Override
            public void onSuccess(List<NoteBean> data) {
                    noteList.clear();
                    noteList.addAll(data);
                    noteAdapter.notifyDataSetChanged();
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

                //显示最新的学期
                tvTerm.setText(Const.termToChinese[termList.get(termList.size()-1).getTerm()]);
                term = termList.get(termList.size()-1).getTerm();
                //显示课程名称
                tvName.setText(notebook.getName());
                showNoteList(notebook.getId());
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
