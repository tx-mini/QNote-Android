package com.ace.qnote.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.ace.network.service.UniversityService;
import com.ace.qnote.R;
import com.ace.qnote.adapter.UniversityAdapter;
import com.ace.qnote.base.BaseActivity;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.List;

import csu.edu.ice.model.University;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChooseUniversityActivity extends BaseActivity {

    RecyclerView rvUniversity;
    UniversityAdapter universityAdapter;
    private List<University> universityList;
    private EditText etUniversity;
    @Override
    public void initParams(Bundle params) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_choose_university;
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public void initView(View view) {
        rvUniversity = findViewById(R.id.rv_university);
        etUniversity = findViewById(R.id.et_univeristy);
    }

    @Override
    public void setListener() {
        findViewById(R.id.iv_back).setOnClickListener(v->finish());
        etUniversity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String university = s.toString();
                List<University> universities = LitePal.where("name like ?",university+"%").find(University.class);
                notifyDataChanged(universities);
            }
        });
    }

    @Override
    public void widgetClick(View v) throws Exception {

    }

    public void notifyDataChanged(List<University> universities){
        List<University> data = universityAdapter.getData();
        data.clear();
        data.addAll(universities);
        universityAdapter.notifyDataSetChanged();
    }

    @Override
    public void doBusiness(Context mContext) {

        universityAdapter = new UniversityAdapter(R.layout.item_text_line, universityList);
        rvUniversity.setAdapter(universityAdapter);
        rvUniversity.setLayoutManager(new LinearLayoutManager(this));
        LitePal.findAllAsync(University.class).listen(new FindMultiCallback() {
            @Override
            public <T> void onFinish(List<T> t) {
                notifyDataChanged((List<University>) t);
                if(t==null) {
                    loadFromServer();
                }
            }
        });

        universityAdapter.setOnItemClickListener((adapter, view, position) -> {
            University university = (University) adapter.getData().get(position);
            Intent intent = new Intent();
            intent.putExtra("universityName",university.getName());
            setResult(200,intent);
            finish();
        });



    }

    private void loadFromServer() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://119.29.166.254:9090/api/university/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            UniversityService universityService = retrofit.create(UniversityService.class);
            universityService.getAllUniversity("")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(universities -> {
                        if(universities!=null && universities.size()>0) {
                            LitePal.saveAll(universities);
                            notifyDataChanged(universities);
                        }else{
                            showToast("未获取到大学信息，请检查网络");
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        showToast("未获取到大学信息，请检查网络");
                    });

    }

}
