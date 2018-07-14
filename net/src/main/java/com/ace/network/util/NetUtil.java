package com.ace.network.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ace.network.service.CourseService;
import com.ace.network.service.NoteService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NetUtil {

    private static Retrofit retrofit;

    private static final int DEFAULT_TIMEOUT = 5;

    public static CourseService courseService;
    public static NoteService noteService;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            return newInstance();
        }
        return retrofit;
    }

    //登陆成功获取到token时，调用一次本方法刷新retrofit的单例，使得能够成功添加请求头
    public static Retrofit newInstance() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);

        OkHttpClient httpClient = builder.build();
        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ConstUrl.HOST)
                .client(httpClient)
                .build();

        courseService = retrofit.create(CourseService.class);
        noteService = retrofit.create(NoteService.class);
        return retrofit;
    }

    public static <T> void doRetrofitRequest(Observable<T> observable, final CallBack<T> callBack) {

            observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<T>() {
                        @Override
                        public void accept(@NonNull T t) throws Exception {
                            callBack.onSuccess(t);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            callBack.onError(throwable);
                        }
                    });

    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


}
