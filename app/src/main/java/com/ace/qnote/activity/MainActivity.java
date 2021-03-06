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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import csu.edu.ice.model.dao.BookBean;
import csu.edu.ice.model.dao.NoteBean;
import csu.edu.ice.model.dao.TermBean;
import csu.edu.ice.model.model.ContentBean;
import csu.edu.ice.model.model.CustomCourse;
import csu.edu.ice.model.model.TermResult;
import me.iwf.photopicker.PhotoPicker;

public class MainActivity extends BaseActivity {
    private LinearLayout layoutLoading;
    private final int REQUEST_CODE_CHOOSE = 1000;
    private ImageView ivEdit, ivTakePhoto;
    private TextView tvNickname;
    private TextView tvTerm;
    private LinearLayout layoutTerm;
    private LinearLayout layoutOtherBook, layoutNewNote, layoutCourseTable, layoutDustbin, layoutStudy;
    private RecyclerView rvNotebook, rvNote;
    private String[] m_upLoadImgPermission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
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
        layoutOtherBook = findViewById(R.id.layout_other_book);
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
        layoutLoading = findViewById(R.id.layout_loading);
    }

    @Override
    public void setListener() {
        tvNickname.setOnClickListener(this);
        tvTerm.setOnClickListener(this);
        layoutTerm.setOnClickListener(this);
        layoutDustbin.setOnClickListener(this);
        layoutStudy.setOnClickListener(this);
        layoutOtherBook.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.tv_term:
                showChooseTermPopwindow();
                break;
            case R.id.layout_other_book:
                showOtherBook();
                break;
            case R.id.layout_course_table:
                if (LitePal.count(CustomCourse.class) > 0) {
                    startActivity(new Intent(this, CourseActivity.class));
                } else {
                    NetUtil.doRetrofitRequest(NetUtil.courseService.getCourseList(Const.OPEN_ID), new CallBack<List<CustomCourse>>() {
                        @Override
                        public void onSuccess(List<CustomCourse> data) {
                            LitePal.saveAll(data);
                            startActivity(new Intent(MainActivity.this, CourseActivity.class));
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
                startActivityForResult(new Intent(this, InformationActivity.class), Const.TO_INFORMATION);
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
                } else {
                    addPic();
                }
                break;
            case R.id.iv_sync:
                if (isSync) {
                    showToast("正在同步中......");
                    return;
                }
                syncToServer();
                break;
        }
    }

    private void showOtherBook() {
        tvName.setText("其他笔记");
        showNoteList("-1");
    }

    private void syncToServer() {

        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
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
        if (unSyncList.size() == 0) {
            syncDataFromServer();
            return;
        }
        NoteBean noteBean = unSyncList.get(0);
        NetUtil.doRetrofitRequest(NetUtil.noteService.addNote(Const.OPEN_ID, noteBean.getBookRef(),
                noteBean.getName(), noteBean.getContent(), noteBean.getIsKeyNote()), new CallBack<RxReturnData>() {
            @Override
            public void onSuccess(RxReturnData data) {
                noteBean.setIsLocal(0);
                noteBean.update(noteBean.get_id());
                unSyncList.remove(0);
                createNote(unSyncList);
            }

            @Override
            public void onError(Throwable throwable) {
                isSync = false;
                ivSync.clearAnimation();
                showToast("同步出差啦！还差" + unSyncList.size() + "篇笔记未同步");
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }


    private void openDustbin() {
        NetUtil.doRetrofitRequest(NetUtil.noteService.getNoteList(Const.OPEN_ID, "", 1, 0), new CallBack<List<NoteBean>>() {
            @Override
            public void onSuccess(List<NoteBean> data) {
                tvName.setText("垃圾桶");
                showNoteList(data);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onFailure(String message) {

            }
        });

    }

    private void openOtherBook() {
        NetUtil.doRetrofitRequest(NetUtil.noteService.getNoteList(Const.OPEN_ID, "-1", 0, 0), new CallBack<List<NoteBean>>() {
            @Override
            public void onSuccess(List<NoteBean> data) {
                tvName.setText("其他笔记");
                showNoteList(data);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }

            @Override
            public void onError(Throwable throwable) {
                showToast("网络可能有问题，请重试！");
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void showNoteList(List<NoteBean> data) {
        Log.d(TAG, "showNoteList: 展示的数据：" + data);
        if (data != null && data.size() > 0) {
            LitePal.deleteAll(NoteBean.class, "bookRef = ?", data.get(0).getBookRef());
            LitePal.saveAll(data);
        }
        noteList.clear();
        noteList.addAll(data);
        noteAdapter.setNewData(noteList);
        updateTip();
    }

    private void updateTip() {
        if (tvName.getText().equals("垃圾桶")) {
            tvNullTip.setText("垃圾桶暂无笔记");
        } else {
            tvNullTip.setText("暂无笔记\n点击下方的按钮创建笔记吧");
        }
        if (noteAdapter.getData() == null || noteAdapter.getData().size() == 0) {
            tvNullTip.setVisibility(View.VISIBLE);
        } else {
            tvNullTip.setVisibility(View.INVISIBLE);
        }
    }

    private CustomPopWindow showWindow(View view) {
        return new CustomPopWindow.PopupWindowBuilder(this)
                .setView(view)//显示的布局
                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                .setBgDarkAlpha(0.7f) // 控制亮度
                .create()//创建PopupWindow
                .showAtLocation(getmContextView(), Gravity.CENTER, 0, 0);//显示PopupWindow
    }

    private void showAddNoteBookPopwindow(int term) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_rename, null);
        EditText etName = view.findViewById(R.id.et_name);
        etName.setHint("请输入笔记本名称");
        CustomPopWindow popWindow = showWindow(view);
        view.findViewById(R.id.btn_ok).setOnClickListener(v -> {

            if (TextUtils.isEmpty(etName.getText().toString())) {
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
                    showToast("创建失败，请稍后再试！");
                }

                @Override
                public void onFailure(String message) {

                }
            });

        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            popWindow.dissmiss();
        });
    }

    private void showChooseTermPopwindow() {
        TermAdapter termAdapter = new TermAdapter(R.layout.item_text_line, termList);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_term, null);
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
            if (bookList.size() > 0) {
                showLatestNoteList(bookList.get(0).getBookId(), false);
            }
            tvName.setText(bookList.get(0).getName());
            notebook = bookList.get(0);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void showLatestNoteList(String bookId, boolean isRubbish) {
        NetUtil.doRetrofitRequest(NetUtil.noteService.getNoteList(Const.OPEN_ID, bookId, 0, 0), new CallBack<List<NoteBean>>() {
            @Override
            public void onSuccess(List<NoteBean> data) {
                Log.d(TAG, "onSuccess: 获取的数据：" + data);
                if (!isRubbish) {
                    Iterator<NoteBean> it = data.iterator();
                    while (it.hasNext()) {
                        NoteBean tempBean = it.next();
                        if (tempBean.getIsRubbish() == 1) {
                            it.remove();
                        }
                    }
                }
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
        String name;
        if (getNowBookBean()!=null){
            name = getNowBookBean().getName();
        }else {
            name = "其他笔记";
        }
        intent.putExtra("course_name", name);
        startActivityForResult(intent, REQUEST_CODE_CHOOSE);
    }

    @Override
    public void doBusiness(Context mContext) {

        Glide.with(this).load(getSharedPreferences(Const.SP_NAME, MODE_PRIVATE).getString("imageUrl", ""))
                .apply(new RequestOptions().circleCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher))
                .into((ImageView) findViewById(R.id.iv_image));

        ((TextView) findViewById(R.id.tv_nickname)).setText(getSharedPreferences(Const.SP_NAME, MODE_PRIVATE).getString("nickname", "加载失败"));


//        if (NetUtil.isNetworkConnected(this)) {
//            //有网络 从服务器请求
//            syncDataFromServer();
//        } else {
//            getDataFromLocal();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Const.MAIN_ACTIVITY_REFRESH){
            if (NetUtil.isNetworkConnected(this)) {
                //有网络 从服务器请求
                syncDataFromServer();
            } else {
                getDataFromLocal();
            }
            Const.MAIN_ACTIVITY_REFRESH = false;
        }
    }

    private void initNoteRecyclerView() {

        noteAdapter = new NoteAdapter(R.layout.item_note, noteList, this, () -> notebook.getTerm(), rootView);
        noteAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            NoteBean noteBean = (NoteBean) adapter.getData().get(position);
            bundle.putString("title", noteBean.getName());
            bundle.putString("noteId", noteBean.getNoteId());
            bundle.putBoolean("isAdd",false);
            startActivity(NoteContentActivity.class, bundle);
        });
        rvNote.setAdapter(noteAdapter);
        rvNote.setLayoutManager(new LinearLayoutManager(this));
        if (noteList == null || noteList.size() == 0) {
            tvNullTip.setVisibility(View.VISIBLE);
        }

        noteAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateTip();
            }
        });
    }

    private void initDrawerRecyclerView() {
        drawerNoteAdapter = new DrawerNoteAdapter(R.layout.item_drawer_note, notebookList);
        rvNotebook.setAdapter(drawerNoteAdapter);
        rvNotebook.setLayoutManager(new LinearLayoutManager(this));
        drawerNoteAdapter.setOnItemClickListener((adapter, view, position) -> {
            List<BookBean> bookList = drawerNoteAdapter.getData();
            BookBean bookbean = bookList.get(position);
            tvName.setText(bookbean.getName());
            notebook = bookbean;
            showLatestNoteList(bookbean.getBookId(), false);
        });
    }

    private void showAddTextPopWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_add_text, null);
        CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(view)//显示的布局
                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                .setBgDarkAlpha(0.7f) // 控制亮度
                .create()//创建PopupWindow
                .showAtLocation(getmContextView(), Gravity.CENTER, 0, 0);//显示PopupWindow

        View btnOk = view.findViewById(R.id.btn_ok);
        View btnCancel = view.findViewById(R.id.btn_cancel);
        EditText editText = view.findViewById(R.id.et_text);
        btnOk.setOnClickListener(v -> {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(editText.getText().toString());
            addTextOrPic(arrayList, true);
            popWindow.dissmiss();
        });

        btnCancel.setOnClickListener(v -> popWindow.dissmiss());
    }

    private void addTextOrPic(ArrayList<String> data, boolean isText) {
        Const.MAIN_ACTIVITY_REFRESH = true;
        BookBean bookBean = getNowBookBean();
        if (bookBean == null) {
            bookBean = new BookBean();
            bookBean.setBookId("-1");
        }
        ArrayList<NoteBean> noteBeans = new ArrayList<>();
        BookBean finalBookBean = bookBean;
        LitePal.where("bookRef = ?", bookBean.getBookId()).findAsync(NoteBean.class).listen(new FindMultiCallback() {
            @Override
            public <T> void onFinish(List<T> t) {
                noteBeans.addAll((List<NoteBean>) t);
                Date nowDate = new Date(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日笔记", Locale.CHINESE);
                NoteBean createBean = new NoteBean(
                        MD5Util.crypt(UUID.randomUUID().toString()),
                        simpleDateFormat.format(nowDate),
                        finalBookBean.getBookId(),
                        Const.OPEN_ID
                );
                for (NoteBean noteBean : noteBeans) {
                    if (noteBean.getIsRubbish()!=1){
                        Date noteDate = new Date(Long.parseLong(noteBean.getCreateTime()));
                        Calendar noteCalendar = Calendar.getInstance();
                        noteCalendar.setTime(noteDate);
                        Calendar nowCalendar = Calendar.getInstance();
                        nowCalendar.setTime(nowDate);
                        if (noteCalendar.get(Calendar.DAY_OF_YEAR) == nowCalendar.get(Calendar.DAY_OF_YEAR)) {
                            createBean = noteBean;
                        }
                    }
                }

                if (createBean.getContent() == null) {
                    addNoteToEmpty(data, isText, createBean);
                } else {
                    addNoteToExist(data, isText, createBean);
                }
            }
        });

    }

    private void addNoteToEmpty(ArrayList<String> data, boolean isText, NoteBean createBean) {
        Map<String, ContentBean.EntityBean> entityBeanMap = new HashMap<>();
        ArrayList<ContentBean.BlocksBean> blocksBeans = new ArrayList<>();
        if (isText) {
            ContentBean.BlocksBean blocksBean = new ContentBean().new BlocksBean();
            blocksBean.setKey(MD5Util.crypt(UUID.randomUUID().toString()).substring(0, 5));
            blocksBean.setDepth(0);
            blocksBean.setInlineStyleRanges(new ArrayList<>());
            blocksBean.setEntityRanges(new ArrayList<>());
            blocksBean.setData(new ContentBean().new BlocksBean().new DataBean());
            blocksBean.setText(data.get(0));
            blocksBean.setType("unstyled");
            blocksBeans.add(blocksBean);
        } else {
            for (int i = 0; i < data.size(); i++) {
                String picPath = data.get(i);
                ContentBean.BlocksBean blocksBean = new ContentBean().new BlocksBean();
                blocksBean.setKey(MD5Util.crypt(UUID.randomUUID().toString()).substring(0, 5));
                blocksBean.setDepth(0);
                blocksBean.setInlineStyleRanges(new ArrayList<>());
                blocksBean.setEntityRanges(new ArrayList<>());
                blocksBean.setData(new ContentBean().new BlocksBean().new DataBean());
                blocksBean.setText(" ");
                blocksBean.setType("atomic");
                ContentBean.BlocksBean.EntityRangesBean entityRangesBean = new ContentBean().new BlocksBean().new EntityRangesBean();
                entityRangesBean.setKey(i);
                entityRangesBean.setLength(1);
                entityRangesBean.setOffset(0);
                blocksBean.getEntityRanges().add(entityRangesBean);
                blocksBeans.add(blocksBean);

                ContentBean.EntityBean entityBean = new ContentBean().new EntityBean();
                ContentBean.EntityBean.DataBean dataBean = new ContentBean().new EntityBean().new DataBean();
                dataBean.setMeta(new ContentBean().new EntityBean().new DataBean().new MetaBean());
                dataBean.setName(MD5Util.crypt(UUID.randomUUID().toString()));
                dataBean.setUrl(picPath);
                dataBean.setType("IMAGE");
                entityBean.setData(dataBean);
                entityBean.setMutability("IMMUTABLE");
                entityBean.setType("IMAGE");
                entityBeanMap.put(i + "", entityBean);
            }
        }
        Gson gson = new Gson();
        ContentBean contentBean = new ContentBean(entityBeanMap, blocksBeans);
        createBean.setContent(gson.toJson(contentBean));
        addNoteInfo(createBean);
    }

    private void addNoteToExist(ArrayList<String> noteData, boolean isText, NoteBean createBean) {
        NetUtil.doRetrofitRequest(NetUtil.getRetrofitInstance().create(NoteService.class).getNoteContent(createBean.getNoteId(), Const.OPEN_ID), new CallBack<NoteBean>() {
            @Override
            public void onSuccess(NoteBean data) {
                String _tempJson = data.getContent();
                Gson gson = new Gson();
                ContentBean contentBean = gson.fromJson(_tempJson, ContentBean.class);
                if (isText) {
                    ContentBean.BlocksBean blocksBean = new ContentBean().new BlocksBean();
                    blocksBean.setKey(MD5Util.crypt(UUID.randomUUID().toString()).substring(0, 5));
                    blocksBean.setDepth(0);
                    blocksBean.setInlineStyleRanges(new ArrayList<>());
                    blocksBean.setEntityRanges(new ArrayList<>());
                    blocksBean.setData(new ContentBean().new BlocksBean().new DataBean());
                    blocksBean.setText(noteData.get(0));
                    blocksBean.setType("unstyled");
                    contentBean.getBlocks().add(blocksBean);
                } else {
                    int imgSize = 0;
                    for (ContentBean.BlocksBean blocksBean : contentBean.getBlocks()) {
                        if (blocksBean.getEntityRanges().size() > 0) {
                            imgSize++;
                        }
                    }
                    for (int i = 0; i < noteData.size(); i++) {
                        String picPath = noteData.get(i);
                        ContentBean.BlocksBean blocksBean = new ContentBean().new BlocksBean();
                        blocksBean.setKey(MD5Util.crypt(UUID.randomUUID().toString()).substring(0, 5));
                        blocksBean.setDepth(0);
                        blocksBean.setInlineStyleRanges(new ArrayList<>());
                        blocksBean.setEntityRanges(new ArrayList<>());
                        blocksBean.setData(new ContentBean().new BlocksBean().new DataBean());
                        blocksBean.setText(" ");
                        blocksBean.setType("atomic");
                        ContentBean.BlocksBean.EntityRangesBean entityRangesBean = new ContentBean().new BlocksBean().new EntityRangesBean();
                        entityRangesBean.setKey(imgSize + i + 1);
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
                        contentBean.getEntityMap().put((imgSize + i + 1) + "", entityBean);
                    }
                }
                createBean.setContent(gson.toJson(contentBean));
                saveNoteInfo(createBean);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void saveNoteInfo(NoteBean createBean) {
        createBean.update(createBean.get_id());
        NetUtil.doRetrofitRequest(NetUtil.noteService.update(Const.OPEN_ID, createBean.getNoteId(),
                createBean.getName(), createBean.getBookRef(), createBean.getIsKeyNote(), 0, true, createBean.getContent()), new CallBack<RxReturnData>() {
            @Override
            public void onSuccess(RxReturnData data) {
                createBean.setIsLocal(0);
                createBean.setSyncTime(System.currentTimeMillis()/1000 + "");
                createBean.setRecentTime(System.currentTimeMillis()/1000 + "");
                createBean.update(createBean.get_id());
                showToast("添加成功");
                Bundle bundle = new Bundle();
                bundle.putString("title", createBean.getName());
                bundle.putString("noteId", createBean.getNoteId());
                bundle.putBoolean("isAdd",true);
                startActivity(NoteContentActivity.class, bundle);
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void addNoteInfo(NoteBean createBean) {
        createBean.saveAsync().listen(new SaveCallback() {
            @Override
            public void onFinish(boolean success) {
            }
        });
        NetUtil.doRetrofitRequest(NetUtil.noteService.addNote(Const.OPEN_ID, createBean.getBookRef(),
                createBean.getName(), createBean.getContent(), createBean.getIsKeyNote()), new CallBack<RxReturnData>() {
            @Override
            public void onSuccess(RxReturnData data) {
                createBean.setIsLocal(0);
                createBean.setSyncTime(System.currentTimeMillis()/1000 + "");
                createBean.setRecentTime(System.currentTimeMillis()/1000 + "");
                createBean.setNoteId(data.getMsg());
                createBean.update(createBean.get_id());
                showToast("添加成功");
                Bundle bundle = new Bundle();
                bundle.putString("title", createBean.getName());
                bundle.putString("noteId", createBean.getNoteId());
                bundle.putBoolean("isAdd",true);
                startActivity(NoteContentActivity.class, bundle);
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void showNoteList(String book_id) {
        noteList = new ArrayList<>();
        showLatestNoteList(book_id, false);
    }

    private void getDataFromLocal() {
        termList = LitePal.findAll(TermBean.class);
        if (termList != null && termList.size() > 0) {
            notebookList = LitePal.where("term = ?", termList.get(termList.size() - 1).getTerm() + "").find(BookBean.class);
            Log.d(TAG, "getDataFromLocal: " + notebookList);
            if (notebookList != null && notebookList.size() > 0) {
                noteList = LitePal.where("bookRef = ?", notebookList.get(0).getBookId()).find(NoteBean.class);

                removeRubbish(noteList);
                notebook = notebookList.get(0);
//                tvName.setText(notebookList.get(0).getName());
                initNoteRecyclerView();
                initDrawerRecyclerView();
//                showNoteList(noteList);
                Log.d(TAG, "getDataFromLocal: " + noteList);


                BookBean nowBookBean = getNowBookBean();
                if (nowBookBean == null) {
                    openOtherBook();
                } else {
                    tvName.setText(nowBookBean.getName());
                    showNoteList(nowBookBean.getBookId());
                }
                layoutLoading.setVisibility(View.GONE);

            } else {
                Toast.makeText(this, "网络状况不佳，请退出重试！", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "网络状况不佳，请退出重试！", Toast.LENGTH_LONG).show();
        }

    }

    private void removeRubbish(List<NoteBean> noteList) {
        if (noteList == null || noteList.size() == 0) return;
        Iterator<NoteBean> it = noteList.iterator();
        while (it.hasNext()) {
            NoteBean noteBean = it.next();
            if (noteBean.getIsRubbish() == 1) {
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

                        notebookList = data.getTermList().get(data.getTermList().size() - 1).getChildren();

                        termList.clear();
                        termList.addAll(data.getTermList());

                        LitePal.deleteAll(TermBean.class);
                        LitePal.saveAll(termList);
                        if (notebookList != null && notebookList.size() > 0) {
                            notebook = notebookList.get(0);
                        } else {
                            showToast("暂无笔记");
                        }

                        //显示最新的学期
                        term = termList.get(termList.size() - 1).getTerm();
                        tvTerm.setText(Const.termToChinese[termList.get(termList.size()-1).getTerm()]);
                        //显示课程名称
//                tvName.setText(notebook.getName());
//                showNoteList(notebook.getBookId());
                        initNoteRecyclerView();
                        initDrawerRecyclerView();

                        if (isSync) {
                            isSync = false;
                            ivSync.clearAnimation();
                            showToast("同步完成！");
                        }

                        BookBean nowBookBean = getNowBookBean();
                        if (nowBookBean == null) {
                            openOtherBook();
                        } else {
                            tvName.setText(nowBookBean.getName());
                            showNoteList(nowBookBean.getBookId());
                        }
                        layoutLoading.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showToast("网络出现了问题，从本地读取笔记！");
                        getDataFromLocal();
                        if (isSync) {
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
        if (resultCode == RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == REQUEST_CODE_CHOOSE)) {
            if (data != null) {
                ArrayList<String> photos = new ArrayList<>();
                if (requestCode == PhotoPicker.REQUEST_CODE) {
                    photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                }else if ( requestCode == REQUEST_CODE_CHOOSE){
                    photos = data.getStringArrayListExtra("picPath");
                }
                if (photos.size()>0){
                    OssUtil.upload(OssUtil.getService(getBaseContext()), photos, new OssListener() {
                        @Override
                        public void onProgress(long progress, long max) {

                        }

                        @Override
                        public void onSuccess(ArrayList<String> url) {
                            addTextOrPic(url, false);
                            showToast("上传成功");
                        }

                        @Override
                        public void onFail() {

                        }
                    });
                }
            }
        } else if (resultCode == Const.LOGOUT && requestCode == Const.TO_INFORMATION) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public BookBean getNowBookBean() {


        List<CustomCourse> courses = LitePal.findAll(CustomCourse.class);
        int week = CommonUtils.getWeek(Const.START_DAY);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        weekday = weekday -1;
        if(weekday==0)weekday= 7;
        int minute = calendar.get(Calendar.MINUTE);
        int nowTime = hour * 100 + minute;
        for (CustomCourse course : courses) {
            if (course.getStartWeek() <= week && course.getEndWeek() >= week && course.getWeekday() == weekday) {
                if (Const.startTimes[course.getStartSection()] <= nowTime && Const.startTimes[course.getStartSection()] + Const.courseDuration >= nowTime) {
                    List<BookBean> books = LitePal.where("term = ? and name = ?", course.getTerm() + "", course.getName()).find(BookBean.class);
                    if (books.size() > 0) {
                        return books.get(0);
                    }
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        Const.MAIN_ACTIVITY_REFRESH = true;
        super.onDestroy();
    }
}
