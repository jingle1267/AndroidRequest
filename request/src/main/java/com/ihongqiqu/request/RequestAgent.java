package com.ihongqiqu.request;

import android.content.Context;
import android.text.TextUtils;
import com.ihongqiqu.request.cache.CacheProvide;
import com.ihongqiqu.request.interceptor.CacheInterceptor;
import com.ihongqiqu.request.interceptor.DownLoadInterceptor;
import com.ihongqiqu.request.interceptor.ParamsInterceptorImpl;
import com.ihongqiqu.request.interceptor.RetryAndChangeIpInterceptor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 初始化／设置全局属性
 * <p>
 * Created by zhenguo on 6/6/17.
 */
public class RequestAgent {

    private static RetrofitHttpService retrofitHttpService;
    /**
     * 这里是application的context
     */
    private static Context mContext;

    /**
     * 初始化方法
     *
     * @param context
     * @param baseUrl
     */
    public static void init(Context context, String baseUrl) {
        RequestAgent.mContext = context.getApplicationContext();
        retrofitHttpService = getRetrofitService(context, baseUrl);
    }

    /**
     * 获取retrofitService
     *
     * @param context
     * @param baseUrl
     * @return
     */
    public static RetrofitHttpService getRetrofitService(Context context, String baseUrl) {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new NullPointerException("BASE_URL can not be null");
        }

        OkHttpClient client = RequestAgent.getOkHttpClient(context.getApplicationContext(), baseUrl, null);

        Retrofit.Builder builder = new Retrofit.Builder();

        builder.addConverterFactory(ScalarsConverterFactory.create());

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        Retrofit retrofit = builder
                .baseUrl(baseUrl)
                .client(client).build();

        return retrofit.create(RetrofitHttpService.class);
    }

    /**
     * 设置显示log
     */
    public static void showLog() {
        RequestConfig.showLog();
    }

    /**
     * 设置隐藏log
     */
    public static void hideLog() {
        RequestConfig.hideLog();
    }

    /**
     * 这里获取的是默认的 retrofit service
     *
     * @return
     */
    public static RetrofitHttpService getRetrofitHttpService() {
        return retrofitHttpService;
    }

    public static void setRetrofitHttpService(RetrofitHttpService retrofitHttpService) {
        RequestAgent.retrofitHttpService = retrofitHttpService;
    }

    public static void addHeader(String key, String value) {
        GlobalParams.getCommonHeaders().put(key, value);
    }

    public static void clearheaders(Map<String, String> headers) {
        GlobalParams.getCommonHeaders().putAll(headers);
    }

    public static void addParam(String key, String value) {
        GlobalParams.getCommonParams().put(key, value);
    }

    public static void clearParams(Map<String, String> headers) {
        GlobalParams.getCommonHeaders().putAll(headers);
    }

    protected static synchronized OkHttpClient getOkHttpClient(final Context context, String baseUrl, List<String> servers) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new DownLoadInterceptor(baseUrl))
                .addInterceptor(new RetryAndChangeIpInterceptor(baseUrl, servers))
                .addInterceptor(new ParamsInterceptorImpl(context))
                .addNetworkInterceptor(new CacheInterceptor())
                .cache(new CacheProvide(context).provideCache())
                .retryOnConnectionFailure(true)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8, TimeUnit.SECONDS)
                .build();
        if (RequestConfig.isShowLog()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            client = client.newBuilder().addInterceptor(logging).build();
        }
        return client;

    }

    public static Context getContext() {
        return mContext;
    }
}
