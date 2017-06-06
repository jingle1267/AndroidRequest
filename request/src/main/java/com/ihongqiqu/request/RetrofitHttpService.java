package com.ihongqiqu.request;

import android.database.Observable;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 基本的网络请求
 * <p>
 * Created by zhenguo on 6/6/17.
 */
public interface RetrofitHttpService {

    @GET()
    Call<String> get(@HeaderMap Map<String, String> headers, @Url String url, @QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST()
    Call<String> post(@HeaderMap Map<String, String> headers, @Url String url, @FieldMap Map<String, String> params);

    @GET()
    Observable<String> Obget(@HeaderMap Map<String, String> headers, @Url String url, @QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST()
    Observable<String> Obpost(@HeaderMap Map<String, String> headers, @Url String url, @FieldMap Map<String, String> params);

    @Streaming
    @GET()
    Observable<ResponseBody> Obdownload(@HeaderMap Map<String, String> headers, @Url String url, @QueryMap Map<String, String> params);

    @Streaming
    @GET()
    Call<ResponseBody> download(@HeaderMap Map<String, String> headers, @Url String url, @QueryMap Map<String, String> params);

}
