package com.ace.network.service;

import java.util.List;

import csu.edu.ice.model.University;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ice on 2018/7/11.
 */

public interface UniversityService {

    @GET("getByUniversityName")
    Observable<List<University>> getAllUniversity(@Query("name") String name);

}
