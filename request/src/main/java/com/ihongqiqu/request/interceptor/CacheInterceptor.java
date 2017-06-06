package com.ihongqiqu.request.interceptor;

import android.text.TextUtils;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络缓存拦截器
 * <p>
 * Created by zhenguo on 6/6/17.
 */
public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        String cache = request.header("Cache-Time");
        if (!TextUtils.isEmpty(cache)) {
            Response response1 = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    //cache for 30 days
                    .header("Cache-Control", "max-age="+cache)
                    .build();
            return response1;
        } else {
            return response;
        }
    }
}
