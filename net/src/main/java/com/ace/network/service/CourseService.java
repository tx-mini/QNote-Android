package com.ace.network.service;

import java.util.List;

import csu.edu.ice.model.model.CustomCourse;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by ice on 2018/7/13.
 */

public interface CourseService {

        @FormUrlEncoded
        @POST("getSub")
        Observable<List<CustomCourse>> getCourseList(@Field("stu_num") String stuId,@Field("password")String password,
                                                     @Field("college") String schoolName,@Field("openid")String openId);

}
