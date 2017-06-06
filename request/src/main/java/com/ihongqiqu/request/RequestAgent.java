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
     * 初始化方法
     *
     * @param context
     * @param baseUrl
     */
    public static void init(Context context, String baseUrl) {
        //接口baseUrl
        if (TextUtils.isEmpty(baseUrl)) {
            throw new NullPointerException("BASE_URL can not be null");
        }
        //解析器

        OkHttpClient client = RequestAgent.getOkHttpClient(context.getApplicationContext(), baseUrl, null);

        Retrofit.Builder builder = new Retrofit.Builder();

        builder.addConverterFactory(ScalarsConverterFactory.create());

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        Retrofit retrofit = builder
                .baseUrl(baseUrl)
                .client(client).build();

        retrofitHttpService =
                retrofit.create(RetrofitHttpService.class);

    }

    public static RetrofitHttpService getRetrofitHttpService() {
        return retrofitHttpService;
    }

    public static void setRetrofitHttpService(RetrofitHttpService retrofitHttpService) {
        RequestAgent.retrofitHttpService = retrofitHttpService;
    }

    private static OkHttpClient okHttpClient;

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
        if (okHttpClient == null) {

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
            //printf logs while  debug
            if (false) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                client = client.newBuilder().addInterceptor(logging).build();
            }
            okHttpClient = client;
        }
        return okHttpClient;

    }

}
