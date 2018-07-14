package com.ace.network.service;

import java.util.List;

import csu.edu.ice.model.dao.BookBean;
import csu.edu.ice.model.dao.NoteBean;
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
    @FormUrlEncoded
    @POST("listBook")
    Observable<TermResult> getTermAndRubbish(@Field("openid") String value);

    @FormUrlEncoded
    @POST("getNote")
    Observable<NoteBean> getNoteContent(@Field("note_id") String noteId, @Field("openid") String openId);

    @FormUrlEncoded
    @POST("listNote")
    Observable<List<NoteBean>> getNoteList(@Field("openid") String openId, @Field("book_id") String bookId, @Field("is_rubbish") int isRubbish, @Field("is_imp") int isImportant);

    Observable<String> move(String noteId, String bookId);


    Observable<String> rename(String noteId, String newName);

    Observable<String> update(String openId, String noteId, String title, String bookId, boolean updateContent, String content, boolean isKeyNote);

    @FormUrlEncoded
    @POST("http://yapi.demo.qunar.com/mock/13512/createBook")
    Observable<BookBean> addBook(@Field("openid") String openId, @Field("term") int term, @Field("name") String name);

    Observable<String> deleteNote(String openId, String noteId);


}
