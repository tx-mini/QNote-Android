package com.ace.network.service;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {

    @FormUrlEncoded
    @POST("register")
    Observable< String > register(@Field("openId") String openId,
                                  @Field("imageUrl") String imageUrl,
                                  @Field("nickname") String nickname);

}
