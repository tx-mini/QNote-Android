package com.ace.network.service;

import java.util.List;

import csu.edu.ice.model.model.CustomCourse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by ice on 2018/7/13.
 */

public interface CourseService {

        @GET("getSub/{stu_num}")
        Observable<List<CustomCourse>> getCourseList(@Path("stu_num") String stuId);

}
