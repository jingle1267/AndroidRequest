package com.ihongqiqu.request;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    Map<String, String> params = new HashMap<>();
    Map<String, String> headers = new HashMap<>();
    String url;
    String path;
    Error mErrorCallBack;
    Success mSuccessCallBack;
    Progress mProgressCallBack;

    Object tag;

    public final static Map<String, Call> CALL_MAP = new HashMap<>();

    public RequestBuilder() {
        this.setParams();
    }

    public RequestBuilder(String url) {
        this.setParams(url);
    }

    public RequestBuilder cacheTime(String time) {
        headers.put("Cache-Time", time);
        return this;
    }

    public RequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    public RequestBuilder savePath(String path) {
        this.path = path;
        return this;
    }

    public RequestBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }


    public RequestBuilder params(Map<String, String> params) {
        if (params != null) {
            this.params.putAll(params);
        }
        return this;
    }

    public RequestBuilder params(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public RequestBuilder headers(Map<String, String> headers) {
        if (headers != null) {
            this.headers.putAll(headers);
        }
        return this;
    }

    public RequestBuilder headers(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public RequestBuilder success(Success success) {
        this.mSuccessCallBack = success;
        return this;
    }

    public RequestBuilder progress(Progress progress) {
        this.mProgressCallBack = progress;
        return this;
    }

    public RequestBuilder error(Error error) {
        this.mErrorCallBack = error;
        return this;
    }


    private void setParams() {
        this.setParams(null);
    }

    private void setParams(String url) {
        isInit();
        this.url = url;
        this.params = new HashMap<>();
    }

    private void isInit() {
        if (RequestAgent.getRetrofitHttpService() == null) {
            throw new NullPointerException("Network has not be initialized");
        }
    }

    /**
     * 添加一个请求
     *
     * @param tag
     * @param url
     * @param call
     */
    private static synchronized void putCall(Object tag, String url, Call call) {
        if (tag == null)
            return;
        synchronized (CALL_MAP) {
            CALL_MAP.put(tag.toString() + url, call);
        }
    }

    /**
     * 取消某个界面都所有请求，或者是取消某个tag的所有请求
     * 如果要取消某个tag单独请求，tag需要转入tag+url
     *
     * @param tag
     */
    public static synchronized void cancel(Object tag) {
        if (tag == null)
            return;
        List<String> list = new ArrayList<>();
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.startsWith(tag.toString())) {
                    CALL_MAP.get(key).cancel();
                    list.add(key);
                }
            }
        }
        for (String s : list) {
            removeCall(s);
        }
    }

    /**
     * 移除某个请求
     *
     * @param url
     */
    private static synchronized void removeCall(String url) {
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.contains(url)) {
                    url = key;
                    break;
                }
            }
            CALL_MAP.remove(url);
        }
    }

    // 判断是否NULL
    public static void error(Context context, String msg) {
        if (TextUtils.isEmpty(msg)) {
            msg = "未知异常";
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String message(String mes) {
        if (TextUtils.isEmpty(mes)) {
            mes = "似乎已断开与互联网连接";
        }

        if (mes.equals("timeout") || mes.equals("SSL handshake timed out")) {
            return "网络请求超时";
        } else {
            return mes;
        }

    }

    private String checkUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("absolute url can not be empty");
        }
        return url;
    }

    /**
     * 检查参数
     *
     * @param params
     * @return
     */
    public static Map<String, String> checkParams(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        //retrofit的params的值不能为null，此处做下校验，防止出错
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                params.put(entry.getKey(), "");
            }
        }
        return params;
    }

    /**
     * 检查http头
     *
     * @param headers
     * @return
     */
    public static Map<String, String> checkHeaders(Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        //retrofit的headers的值不能为null，此处做下校验，防止出错
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getValue() == null) {
                headers.put(entry.getKey(), "");
            }
        }
        return headers;
    }

    public void get() {
        Call<String> call = RequestAgent.getRetrofitHttpService().get(checkHeaders(headers), checkUrl(this.url), checkParams(params));
        putCall(tag, url, call);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("RequestBuilder", response.body().toString());
                if (response.code() == 200) {
                    if (mSuccessCallBack != null) {
                        mSuccessCallBack.onSuccess(response.body().toString());
                    }
                } else {
                    if (mErrorCallBack != null) {
                        mErrorCallBack.onError(response.code(), message(response.message()), null);
                    }
                }
                if (tag != null)
                    removeCall(url);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("RequestBuilder", call.toString());
                if (mErrorCallBack != null) {
                    mErrorCallBack.onError(200, message(t.getMessage()), t);
                }
                if (tag != null)
                    removeCall(url);
            }
        });
    }

    public void post() {
        Call<String> call = RequestAgent.getRetrofitHttpService().post(checkHeaders(headers), checkUrl(this.url), checkParams(params));
        putCall(tag, url, call);
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
                if (tag != null)
                    removeCall(url);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (mErrorCallBack != null) {
                    mErrorCallBack.onError(200, message(t.getMessage()), t);
                }
                if (tag != null)
                    removeCall(url);
            }
        });
    }

//    //下载
//    public void download() {
//        this.url = checkUrl(this.url);
//        this.params = checkParams(this.params);
//        this.headers.put(Constant.DOWNLOAD, Constant.DOWNLOAD);
//        this.headers.put(Constant.DOWNLOAD_URL, this.url);
//        final Call call = getNetworkService().download(checkHeaders(headers), url, checkParams(params));
//        putCall(tag, url, call);
//        Observable<ResponseBody> observable = Observable.create(new Observable.OnSubscribe<ResponseBody>() {
//            @Override
//            public void call(final Subscriber<? super ResponseBody> subscriber) {
//                call.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        subscriber.onNext(response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
////                        mErrorCallBack.Error(t);
//                    }
//                });
//            }
//        });
//
//        observable.observeOn(Schedulers.io())
////                    .subscribe(body -> WriteFileUtil.writeFile(body, path, mProgressCallBack, mSuccessCallBack, mErrorCallBack), t -> {
////                                mErrorCallBack.Error(t);
////                            }
////                    );
//                .subscribe(new Action1<ResponseBody>() {
//                               @Override
//                               public void call(ResponseBody responseBody) {
//                                   WriteFileUtil.writeFile(responseBody, path, mProgressCallBack, mSuccessCallBack, mErrorCallBack);
//                               }
//                           },
//                        new Action1<Throwable>() {
//                            @Override
//                            public void call(Throwable throwable) {
////                                mErrorCallBack.Error(throwable);
//                            }
//                        });
//    }


}
