package com.ihongqiqu.request.interceptor;

import android.content.Context;
import android.util.Log;
import com.ihongqiqu.request.GlobalParams;
import java.io.IOException;
import java.util.Map;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ParamsInterceptorImpl implements Interceptor {

    private Context context;

    public ParamsInterceptorImpl(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        RequestBody requestBody = original.body();

        // 新的请求
        Request.Builder requestBuilder = original.newBuilder();
        Request newRequest = null;

        // 设置全局header
        for (String key : GlobalParams.getCommonHeaders().keySet()) {
            String value = GlobalParams.getCommonHeaders().get(key);
            requestBuilder.addHeader(key, value);
        }

        Map<String, String> params = GlobalParams.getCommonParams();

        //POST请求时接口参数存放在RequestBody中
        if (requestBody != null) {

            //表单参数
            if (requestBody instanceof FormBody) {
                FormBody oldFormBody = (FormBody) requestBody;
                FormBody.Builder builder = new FormBody.Builder();

                //添加新增参数
                for (int i = 0; i < oldFormBody.size(); i++) {
                    builder.addEncoded(oldFormBody.encodedName(i), oldFormBody.encodedValue(i));
                    //如果新增的参数和公共参数key一致，移除公共参数key
                    if (params.containsKey(oldFormBody.encodedName(i))) {
                        params.remove(oldFormBody.encodedName(i));
                    }
                }

                //添加公共参数
                for (String key : params.keySet()) {
                    builder.addEncoded(key, params.get(key));
                }

                requestBuilder.method(original.method(), builder.build());
            } else if (requestBody instanceof MultipartBody) {//文件参数

                MultipartBody body = ((MultipartBody) requestBody);


                MultipartBody.Builder builder = new MultipartBody.Builder();

                //添加新增参数
                for (int i = 0; i < body.size(); i++) {
                    builder.addPart(body.part(i));
                }

                //添加公共参数
                for (String key : params.keySet()) {
                    builder.addPart(MultipartBody.Part.createFormData(key, params.get(key)));
                }


                requestBuilder.method(original.method(), builder.build());

            }

            newRequest = requestBuilder.build();

        } else {//GET请求参数拼接在URL上

            HttpUrl.Builder builder = original.url().newBuilder();
            //添加公共参数
            for (String key : params.keySet()) {
//                if (key.equals("accesstoken") && original.url().queryParameter(key) != null) {
//                    continue;
//                }
                builder.addEncodedQueryParameter(key, params.get(key));
            }

            newRequest = requestBuilder
                    .url(builder.build())
                    .build();
        }


        Log.d("API_LOG", newRequest.toString());

        return chain.proceed(newRequest);
    }
}
