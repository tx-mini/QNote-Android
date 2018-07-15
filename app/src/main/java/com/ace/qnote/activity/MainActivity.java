package com.ace.qnote.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ace.network.service.NoteService;
import com.ace.network.util.CallBack;
import com.ace.network.util.NetUtil;
import com.ace.network.util.RxReturnData;
import com.ace.qnote.R;
import com.ace.qnote.adapter.DrawerNoteAdapter;
import com.ace.qnote.adapter.NoteAdapter;
import com.ace.qnote.adapter.TermAdapter;
import com.ace.qnote.base.BaseActivity;
import com.ace.qnote.util.CommonUtils;
import com.ace.qnote.util.Const;
import com.ace.qnote.util.MD5Util;
import com.ace.qnote.util.oss.OssListener;
import com.ace.qnote.util.oss.OssUtil;
import com.ace.qnote.util.permission.ActionCallBackListener;
import com.ace.qnote.util.permission.RxPermissionUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.zhouwei.library.CustomPopWindow;
import com.google.gson.Gson;

import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;
import org.litepal.crud.callback.SaveCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import csu.edu.ice.model.dao.BookBean;
import csu.edu.ice.model.dao.NoteBean;
import csu.edu.ice.model.dao.TermBean;
import csu.edu.ice.model.model.ContentBean;
import csu.edu.ice.model.model.CustomCourse;
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
    private List<NoteBean> noteList;
    private BookBean notebook;
    private TextView tvName;
    private DrawerLayout drawerLayout;
    private int term;
    private TextView tvNullTip;
    private ImageView ivSync;
    private boolean isSync;

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
        drawerLayout = findViewById(R.id.drawer_layout);
        tvNullTip = findViewById(R.id.tv_null_tip);
        ivSync = findViewById(R.id.iv_sync);
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
        ivSync.setOnClickListener(this);
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
                if(LitePal.count(CustomCourse.class)>0){
                    startActivity(new Intent(this,CourseActivity.class));
                }else{
                    NetUtil.doRetrofitRequest(NetUtil.courseService.getCourseList(Const.OPEN_ID), new CallBack<List<CustomCourse>>() {
                        @Override
                        public void onSuccess(List<CustomCourse> data) {
                            LitePal.saveAll(data);
                            startActivity(new Intent(MainActivity.this,CourseActivity.class));
                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onFailure(String message) {

                        }
                    });
                }
                break;
            case R.id.layout_dustbin:
                openDustbin();
                break;
            case R.id.layout_new_notebook:
                showAddNoteBookPopwindow(term);
                break;
            case R.id.layout_study:
                break;

            case R.id.tv_nickname:
            case R.id.iv_image:
                startActivityForResult(new Intent(this,InformationActivity.class),Const.TO_INFORMATION);
                break;
            case R.id.iv_edit:
                showAddTextPopWindow();
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
            case R.id.iv_sync:
                if(isSync){
                    showToast("正在同步中......");
                    return;
                }
                syncToServer();
                break;
        }
    }

    private void syncToServer() {

        RotateAnimation rotateAnimation = new RotateAnimation(0f,360f,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        ivSync.startAnimation(rotateAnimation);
        showToast("正在同步......");
        isSync = true;
        //查询所有未同步的
        List<NoteBean> unSyncList = LitePal.where("isLocal = ?", "1").find(NoteBean.class);
        //进行笔记创建，或者更新
        createNote(unSyncList);

    }

    private void createNote(List<NoteBean> unSyncList) {
        if(unSyncList.size() == 0){
            syncDataFromServer();
            return;
        }
        NoteBean noteBean = unSyncList.get(0);
        NetUtil.doRetrofitRequest(NetUtil.noteService.addNote(Const.OPEN_ID, noteBean.getBookRef(),
                noteBean.getName(), noteBean.getContent(), noteBean.getIsKeyNote()), new CallBack<RxReturnData>() {
            @Override
            public void onSuccess(RxReturnData data) {
                noteBean.setLocal(0);
                noteBean.update(noteBean.get_id());
                unSyncList.remove(0);
                createNote(unSyncList);
            }

            @Override
            public void onError(Throwable throwable) {
                isSync = false;
                ivSync.clearAnimation();
                showToast("同步出差啦！还差"+unSyncList.size()+"篇笔记未同步");
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }


    private void openDustbin() {
        NetUtil.doRetrofitRequest(NetUtil.noteService.getNoteList(Const.OPEN_ID,"",1,0), new CallBack<List<NoteBean>>() {
            @Override
            public void onSuccess(List<NoteBean> data) {
                showNoteList(data);
                drawerLayout.closeDrawer(Gravity.LEFT);
                tvName.setText("垃圾桶");
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void showNoteList(List<NoteBean> data) {

        if(tvName.getText().equals("垃圾桶")){
            tvNullTip.setText("垃圾桶暂无笔记");
        }else{
            tvNullTip.setText("暂无笔记\n点击下方的按钮创建笔记吧");
        }
        if(data ==null || data.size() == 0){
            tvNullTip.setVisibility(View.VISIBLE);
        }else{
            tvNullTip.setVisibility(View.INVISIBLE);
            LitePal.deleteAll(NoteBean.class,"bookRef = ?" ,data.get(0).getBookRef());
            LitePal.saveAll(data);
        }
        noteList.clear();
        noteList.addAll(data);
        noteAdapter.notifyDataSetChanged();
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

    private void showAddNoteBookPopwindow(int term) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_rename,null);
        EditText etName = view.findViewById(R.id.et_name);
        etName.setHint("请输入笔记本名称");
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
                    popWindow.dissmiss();
                    showToast("创建成功！");
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
        CustomPopWindow popWindow = showWindow(view);
        RecyclerView recyclerView = view.findViewById(R.id.rv_term);
        recyclerView.setAdapter(termAdapter);
        termAdapter.setOnItemClickListener((adapter, view1, position) -> {
            TermBean termBean = termList.get(position);
            tvTerm.setText(Const.termToChinese[termBean.getTerm()]);
            List<BookBean> bookList = LitePal.where("term = ?", termBean.getTerm() + "").find(BookBean.class);
            drawerNoteAdapter.setNewData(bookList);

            popWindow.dissmiss();
            drawerLayout.closeDrawer(Gravity.LEFT);
            term = termBean.getTerm();
            if(bookList.size()>0) {
                showLatestNoteList(bookList.get(0).getId());
            }
            tvName.setText(bookList.get(0).getName());
            notebook = bookList.get(0);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void showLatestNoteList(String  bookId){
        NetUtil.doRetrofitRequest(NetUtil.noteService.getNoteList(Const.OPEN_ID,bookId,0,0), new CallBack<List<NoteBean>>() {
            @Override
            public void onSuccess(List<NoteBean> data) {
                showNoteList(data);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(MainActivity.this, "出错了!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {

            }
        });
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


        if(NetUtil.isNetworkConnected(this)){
            //有网络 从服务器请求
            syncDataFromServer();
        }else {
            getDataFromLocal();
        }
    }
    private void initNoteRecyclerView() {

        noteAdapter = new NoteAdapter(R.layout.item_note,noteList,this,notebook,rootView);
        noteAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            NoteBean noteBean = (NoteBean) adapter.getData().get(position);
            bundle.putString("title", noteBean.getName());
            bundle.putString("noteId", noteBean.getId());
            startActivity(NoteContentActivity.class,bundle);
        });
        rvNote.setAdapter(noteAdapter);
        rvNote.setLayoutManager(new LinearLayoutManager(this));
        if(noteList == null || noteList.size()==0){
            tvNullTip.setVisibility(View.VISIBLE);
        }
    }

    private void initDrawerRecyclerView() {
        drawerNoteAdapter = new DrawerNoteAdapter(R.layout.item_drawer_note,notebookList);
        rvNotebook.setAdapter(drawerNoteAdapter);
        rvNotebook.setLayoutManager(new LinearLayoutManager(this));
        drawerNoteAdapter.setOnItemClickListener((adapter, view, position) -> {
            List<BookBean> bookList = drawerNoteAdapter.getData();
            BookBean bookbean = bookList.get(position);
            tvName.setText(bookbean.getName());
            notebook = bookbean;
            showLatestNoteList(bookbean.getId());
        });
    }

    private void showAddTextPopWindow() {
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
        btnOk.setOnClickListener(v -> {
            addTextOrPic(editText.getText().toString(),true);
            popWindow.dissmiss();
        });

        btnCancel.setOnClickListener(v -> popWindow.dissmiss());
    }

    private void addTextOrPic(String data, boolean isText) {
        BookBean bookBean = getNowBookBean();
        if (bookBean==null){
            bookBean = notebook;
        }
        if (bookBean != null){
            ArrayList<NoteBean> noteBeans = new ArrayList<>();
            LitePal.where("bookRef = ?",bookBean.getId()).findAsync(NoteBean.class).listen(new FindMultiCallback() {
                @Override
                public <T> void onFinish(List<T> t) {
                    noteBeans.addAll((List<NoteBean>) t);
                }
            });
            Date nowDate = new Date(System.currentTimeMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日笔记", Locale.CHINESE);
            NoteBean createBean = new NoteBean(
                    MD5Util.crypt(UUID.randomUUID().toString()),
                    simpleDateFormat.format(nowDate),
                    bookBean.getId(),
                    Const.OPEN_ID
            );
            for (NoteBean noteBean : noteBeans) {
                Date noteDate = new Date(Long.parseLong(noteBean.getCreateTime()));
                Calendar noteCalendar = Calendar.getInstance();
                noteCalendar.setTime(noteDate);
                Calendar nowCalendar = Calendar.getInstance();
                nowCalendar.setTime(nowDate);
                if (noteCalendar.get(Calendar.DAY_OF_YEAR) == nowCalendar.get(Calendar.DAY_OF_YEAR)){
                    createBean = noteBean;
                }
            }

            ContentBean.BlocksBean blocksBean = new ContentBean().new BlocksBean();
            blocksBean.setKey(MD5Util.crypt(UUID.randomUUID().toString()).substring(0,5));
            blocksBean.setText(data);
            blocksBean.setType("unstyled");
            blocksBean.setDepth(0);
            blocksBean.setInlineStyleRanges(new ArrayList<>());
            blocksBean.setEntityRanges(new ArrayList<>());
            blocksBean.setData(new ContentBean().new BlocksBean().new DataBean());
            if (isText){
                if (createBean.getContent()==null){
                    ArrayList<ContentBean.BlocksBean> blocksBeans = new ArrayList<>();
                    blocksBeans.add(blocksBean);
                    ContentBean contentBean = new ContentBean(new HashMap<String,ContentBean.EntityBean>(),blocksBeans);
                    Gson gson = new Gson();
                    createBean.setContent(gson.toJson(contentBean));
                }
            }

            createBean.saveAsync().listen(new SaveCallback() {
                @Override
                public void onFinish(boolean success) {
                }
            });
            NoteBean finalCreateBean = createBean;
            NetUtil.doRetrofitRequest(NetUtil.noteService.addNote(Const.OPEN_ID, createBean.getBookRef(),
                    createBean.getName(), createBean.getContent(), createBean.getIsKeyNote()), new CallBack<RxReturnData>() {
                @Override
                public void onSuccess(RxReturnData data) {
                    finalCreateBean.setLocal(0);
                    finalCreateBean.setSyncTime(System.currentTimeMillis()+"");
                    finalCreateBean.update(finalCreateBean.get_id());
                }

                @Override
                public void onError(Throwable throwable) {
                }

                @Override
                public void onFailure(String message) {

                }
            });

        }

    }

    private void showNoteList(String book_id){
        noteList = new ArrayList<>();
        showLatestNoteList(book_id);
    }

    private void getDataFromLocal() {
        termList = LitePal.findAll(TermBean.class);
        if(termList!=null && termList.size()>0) {
            notebookList = LitePal.where("term = ?", termList.get(termList.size() - 1).getTerm() + "").find(BookBean.class);
            Log.d(TAG, "getDataFromLocal: "+notebookList);
            if(notebookList!=null && notebookList.size()>0) {
                noteList = LitePal.where("bookRef = ?", notebookList.get(0).getId()).find(NoteBean.class);

                removeRubbish(noteList);
                notebook = notebookList.get(0);
                tvName.setText(notebookList.get(0).getName());
                initNoteRecyclerView();
                initDrawerRecyclerView();
                showNoteList(noteList);
                Log.d(TAG, "getDataFromLocal: "+noteList);
            }else{
                Toast.makeText(this, "网络状况不佳，请退出重试！", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "网络状况不佳，请退出重试！", Toast.LENGTH_LONG).show();
        }

    }

    private void removeRubbish(List<NoteBean> noteList) {
        if(noteList==null ||noteList.size() == 0)return;
        Iterator<NoteBean> it = noteList.iterator();
        while (it.hasNext()){
            NoteBean noteBean = it.next();
            if(noteBean.getIsRubbish()==1){
                it.remove();
            }
        }
    }

    private void syncDataFromServer() {
        NetUtil.doRetrofitRequest(NetUtil.getRetrofitInstance().create(NoteService.class).getTermAndRubbish(Const.OPEN_ID),
                new CallBack<TermResult>() {
            @Override
            public void onSuccess(TermResult data) {
                notebookList.clear();
                Collections.sort(data.getTermList(), (o1, o2) -> o1.getTerm() - o2.getTerm());
                for (TermBean termBean : data.getTermList()) {
                    for (BookBean bookBean : termBean.getChildren()) {
                        bookBean.setTerm(termBean.getTerm());
                    }
                    notebookList.addAll(termBean.getChildren());
                }

                LitePal.deleteAll(BookBean.class);
                LitePal.saveAll(notebookList);

                notebookList = data.getTermList().get(data.getTermList().size()-1).getChildren();

                termList.clear();
                termList.addAll(data.getTermList());

                LitePal.deleteAll(TermBean.class);
                LitePal.saveAll(termList);
                if(notebookList!=null && notebookList.size()>0) {
                    notebook = notebookList.get(0);
                }else{
                    showToast("暂无笔记");
                }

                //显示最新的学期
                tvTerm.setText(Const.termToChinese[termList.get(termList.size()-1).getTerm()]);
                term = termList.get(termList.size()-1).getTerm();
                //显示课程名称
                tvName.setText(notebook.getName());
                showNoteList(notebook.getId());
                initNoteRecyclerView();
                initDrawerRecyclerView();

                if(isSync){
                    isSync = false;
                    ivSync.clearAnimation();
                    showToast("同步完成！");
                }

            }

            @Override
            public void onError(Throwable throwable) {
                showToast( "网络出现了问题，从本地读取笔记！");
                getDataFromLocal();
                if(isSync){
                    isSync = false;
                    ivSync.clearAnimation();
                    showToast("同步失败，请检查网络！");
                }
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
                OssUtil.upload(OssUtil.getService(getBaseContext()), photos, new OssListener() {
                    @Override
                    public void onProgress(long progress, long max) {

                    }

                    @Override
                    public void onSuccess(ArrayList<String> url) {
                        showToast("上传成功");
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }
        }else if(resultCode == Const.LOGOUT && requestCode == Const.TO_INFORMATION){
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
                drawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            }else{
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode,event);
    }

    public BookBean getNowBookBean(){

        List<CustomCourse> courses = LitePal.findAll(CustomCourse.class);
        int week = getWeek(Const.START_DAY);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int nowTime = hour*100+minute;
        for (CustomCourse course : courses) {
            if(course.getStartWeek()<=week &&course.getEndWeek()>=week){
                if(Const.startTimes[course.getStartSection()]<=nowTime && Const.startTimes[course.getStartSection()]+Const.courseDuration>=nowTime){
                    List<BookBean> books = LitePal.where("term = ? and name = ?", course.getTerm() + "", course.getName()).find(BookBean.class);
                    if(books.size()>0){
                        return books.get(0);
                    }
                    return null;
                }
            }
        }
        return null;
    }

    public static int getWeek(String firstDay){

        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date past = format.parse(firstDay);
            int days = differentDays(past,now);

            int week = days/7 + 1;

            return week;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static int differentDays(Date date1,Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2) {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++) {
                if(i%4==0 && i%100!=0 || i%400==0) {
                    timeDistance += 366;
                }
                else {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        }
        else {
            return day2-day1;
        }
    }

}
