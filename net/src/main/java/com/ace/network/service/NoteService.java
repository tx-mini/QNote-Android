package com.ace.network.service;

import com.ace.network.util.RxReturnData;


import csu.edu.ice.model.dao.NoteContentBean;
import csu.edu.ice.model.model.TermResult;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NoteService {

//    @FormUrlEncoded
//    @POST("http://yapi.demo.qunar.com/mock/13512/getCategories/%7Bid%7D")
//    Observable<RxReturnData<TermResult>> getTermAndRubbish(@Field("key") String value);
    //TODO:因为测试数据需要，所以暂时用GET，换成真实数据要用上面注释掉的那个方法
    @GET("http://yapi.demo.qunar.com/mock/13512/getCategories/%7Bid%7D")
    Observable<RxReturnData<TermResult>> getTermAndRubbish(@Query("key") String value);



    @GET("http://yapi.demo.qunar.com/mock/13512/note/index.php/mini/getNote/%7Bid%7D")
    Observable<NoteContentBean> getNoteContent(@Query("key") String value);
}
