package com.ace.network.service;

import com.ace.network.util.RxReturnData;


import csu.edu.ice.model.model.TermResult;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NoteService {

    @FormUrlEncoded
    @POST("http://yapi.demo.qunar.com/mock/13512/getCategories/%7Bid%7D")
    Observable<RxReturnData<TermResult>> getTermAndRubbish(@Field("key") String value);

}
