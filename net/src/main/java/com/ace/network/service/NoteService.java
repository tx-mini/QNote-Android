package com.ace.network.service;

import com.ace.network.util.RxReturnData;

import java.util.List;

import csu.edu.ice.model.dao.BookBean;
import csu.edu.ice.model.dao.NoteBean;
import csu.edu.ice.model.model.TermResult;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NoteService {

    //    @FormUrlEncoded
//    @POST("http://yapi.demo.qunar.com/mock/13512/getCategories/%7Bid%7D")
//    Observable<RxReturnData<TermResult>> getTermAndRubbish(@Field("key") String value);
    @FormUrlEncoded
    @POST("listBook")
    Observable<TermResult> getTermAndRubbish(@Field("openid") String value);

    @FormUrlEncoded
    @POST("getNote")
    Observable<NoteBean> getNoteContent(@Field("note_id") String noteId, @Field("openid") String openId);

    @FormUrlEncoded
    @POST("listNote")
    Observable<List<NoteBean>> getNoteList(@Field("openid") String openId, @Field("book_id") String bookId, @Field("is_rubbish") int isRubbish, @Field("is_imp") int isImportant);

    @FormUrlEncoded
    @POST("modNote")
    Observable<RxReturnData> update(@Field("openid") String openId, @Field("note_id") String noteId, @Field("name") String title, @Field("book_id") String bookId,
                                    @Field("is_imp") int isImportant, @Field("is_rubbish") int isRubbish,
                                    @Field("is_bool") boolean updateContent, @Field("content") String content);

    @FormUrlEncoded
    @POST("http://yapi.demo.qunar.com/mock/13512/createBook")
    Observable<BookBean> addBook(@Field("openid") String openId, @Field("term") int term, @Field("name") String name);

    @FormUrlEncoded
    @POST("rmNote/{openid}/{note_id}")
    Observable<String> deleteNote(@Path("openid") String openId,@Path("note_id") String noteId);

    @FormUrlEncoded
    @POST("createNote")
    Observable<RxReturnData> addNote(@Field("openid") String openid,@Field("book_id")String bookId,
                                     @Field("name") String name,@Field("content")String content,@Field("is_imp")int isImportant);


}
