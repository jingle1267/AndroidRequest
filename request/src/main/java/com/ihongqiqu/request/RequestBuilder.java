package com.ihongqiqu.request;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 网络请求构造器
 * <p>
 * Created by zhenguo on 6/6/17.
 */
public class RequestBuilder {

    private String url;
    private String path;
    private Map<String, String> params = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();

    private String requestType = "POST";

    private Error mErrorCallBack;
    private Success mSuccessCallBack;
    private Progress mProgressCallBack;


    private Object tag;

    public RequestBuilder() {
    }

    public RequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    public RequestBuilder path(String path) {
        this.path = path;
        return this;
    }

    public RequestBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    public RequestBuilder params(Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            this.params.putAll(params);
        }
        return this;
    }

    public RequestBuilder param(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public RequestBuilder headers(Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            this.headers.putAll(headers);
        }
        return this;
    }

    public RequestBuilder header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    /**
     * 设置请求成功的回掉
     * @param success
     * @return
     */
    public RequestBuilder success(Success success) {
        this.mSuccessCallBack = success;
        return this;
    }

    /**
     * 进度回掉
     * @param progress
     * @return
     */
    public RequestBuilder progress(Progress progress) {
        this.mProgressCallBack = progress;
        return this;
    }

    /**
     * 设置请求失败的回掉
     * @param error
     * @return
     */
    public RequestBuilder error(Error error) {
        this.mErrorCallBack = error;
        return this;
    }

    /**
     * 网路请求协议 目前支持get post，默认是most
     *
     * @param requestType
     * @return
     */
    public RequestBuilder type(String requestType) {
        this.requestType = requestType;
        return this;
    }

    public void build() {
        if (RequestAgent.getContext() == null) {
            throw new NullPointerException("Network has not be initialized");
        }

        Call<String> call;

        final String path = RequestManager.checkUrl(this.path);
        Map<String, String> safeHeaders = RequestManager.checkHeaders(headers);
        Map<String, String> safeParams = RequestManager.checkParams(params);

        RetrofitHttpService retrofitHttpService;

        if (TextUtils.isEmpty(url)) {
            retrofitHttpService = RequestAgent.getRetrofitHttpService();
        } else {
            retrofitHttpService = RequestAgent.getRetrofitService(RequestAgent.getContext(), url);
        }

        if ("POST".equalsIgnoreCase(requestType)) {
            call = retrofitHttpService.post(safeHeaders, path, safeParams);
        } else if ("GET".equalsIgnoreCase(requestType)) {
            call = retrofitHttpService.get(safeHeaders, path, safeParams);
        } else {
            call = retrofitHttpService.get(safeHeaders, path, safeParams);
        }

        RequestManager.putCall(tag, path, call);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    if (mSuccessCallBack != null) {
                        mSuccessCallBack.onSuccess(response.body().toString());
                    }
                } else {
                    if (mErrorCallBack != null) {
                        mErrorCallBack.onError(response.code(), message(response.message()), null);
                    }
                }
                if (tag != null) {
                    RequestManager.removeCall(path);
                }
                if (mProgressCallBack != null) {
                    mProgressCallBack.onProgress(1.0f);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (mErrorCallBack != null) {
                    mErrorCallBack.onError(200, message(t.getMessage()), t);
                }
                if (tag != null) {
                    RequestManager.removeCall(path);
                }
                if (mProgressCallBack != null) {
                    mProgressCallBack.onProgress(1.0f);
                }
            }

            private String message(String mes) {
                if (TextUtils.isEmpty(mes)) {
                    mes = "网络连接未知错误";
                }

                if (mes.equals("timeout") || mes.equals("SSL handshake timed out")) {
                    return "网络请求超时";
                } else {
                    return mes;
                }

            }
        });
    }

}
