package com.ace.network.service;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {

    @FormUrlEncoded
    @POST("register")
    Observable<Object> register(@Field("openid") String openId,
                                  @Field("logo_url") String imageUrl,
                                  @Field("nick_name") String nickname);

}
