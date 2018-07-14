package com.ace.network.service;

import com.ace.network.util.RxReturnData;

import java.util.List;

import csu.edu.ice.model.dao.BookBean;
import csu.edu.ice.model.dao.NoteBean;
import csu.edu.ice.model.dao.RubbishBean;
import csu.edu.ice.model.model.TermResult;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NoteService {

//    @FormUrlEncoded
//    @POST("http://yapi.demo.qunar.com/mock/13512/getCategories/%7Bid%7D")
//    Observable<RxReturnData<TermResult>> getTermAndRubbish(@Field("key") String value);
    //TODO:因为测试数据需要，所以暂时用GET，换成真实数据要用上面注释掉的那个方法
    @GET("http://yapi.demo.qunar.com/mock/13512/getCategories/%7Bid%7D")
    Observable<TermResult> getTermAndRubbish(@Query("key") String value);



    @GET("http://yapi.demo.qunar.com/mock/13512/note/index.php/mini/getNote/%7Bid%7D")
    Observable<NoteBean> getNoteContent(@Query("key") String value);

    @GET("http://yapi.demo.qunar.com/mock/13512/getNoteList/{id}")
    Observable<RxReturnData<List<NoteBean>>> getNote(@Path("id") String id);

    Observable<String> move(String noteId,String bookId);

    Observable<String> rename(String noteId,String newName);

    Observable<String> update(String openId, String noteId,String title,String bookId, boolean updateContent, String content, boolean isKeyNote);

    Observable<BookBean> addBook(String openId, int term, String name);

    @FormUrlEncoded
    @POST("/listNote")
    Observable<List<NoteBean>> getNoteList(@Field("openid") String openId,@Field("book_id") String bookId,@Field("is_rubbish") int isRubbish,@Field("is_imp") int is_imp);
}
