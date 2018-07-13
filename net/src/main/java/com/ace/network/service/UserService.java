package com.ace.network.service;

import csu.edu.ice.model.model.University;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {

//    @POST(ConstUrl.USER_REGISTER)
//    Observable< RxReturnData<UserData> > register(@Query("phoneNumber") String phoneNumber,
//                                                  @Query("password") String password);
//
//    @POST(ConstUrl.USER_LOGIN)
//    Observable< RxReturnData<UserData> > login(@Query("phoneNumber") String phoneNumber,
//                                               @Query("password") String password);
//    @POST(ConstUrl.USER_CHANGE_PASSWORD)
//    Observable<RxReturnData<String>> changepassword(@Query("userId") String userId,
//                                                    @Query("oldPsd") String oldPsd,
//                                                    @Query("newPsd") String newPsd);
//    @GET(ConstUrl.USER_CHANG_EMAIL)
//    Observable< RxReturnData<String> > changemail(@Query("userId") String userId,
//                                                  @Query("mail") String mail);
//    @GET(ConstUrl.USER_CHANGE_USERNAME)
//    Observable< RxReturnData<String> > changeusename(@Query("userId") String userId,
//                                                     @Query("username") String username);
//    @GET(ConstUrl.USER_GET_INFO)
//    Observable< RxReturnData<UserData> > getUserInfo(@Query("userId") Integer userId);


    @FormUrlEncoded
    @POST("")
    Observable<University> updateUniversity(@Field("id") String id);

}
