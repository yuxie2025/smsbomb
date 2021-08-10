package com.yuxie.smsbomb.api;

import com.apkupdate.widget.ApkVersionModel;
import com.baselib.basebean.BaseRespose;
import com.yuxie.smsbomb.bean.SmsApi;

import java.util.List;
import java.util.Map;

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by luo on 2018/3/3.
 */
public interface ServerApiService {
    @FormUrlEncoded
    @POST("")
    Observable<Result<String>> getSmsApi(@Url String url, @FieldMap Map<String, String> options);

    @GET("")
    Observable<Result<String>> getSmsApi(@Url String url);

    @GET("")
    Observable<String> getUrl(@Url String url);


//    https://raw.githubusercontent.com/yuxie2025/smsbomb/master/
    /**
     * 升级app
     */
    @GET("/yuxie2025/smsbomb/master//download/updateSmsBombApp.json")
    Observable<BaseRespose<ApkVersionModel>>
    updateApp();


    @GET("/yuxie2025/smsbomb/master//download/smsApi.json")
    Observable<BaseRespose<List<SmsApi>>>
    smsApi();

}
