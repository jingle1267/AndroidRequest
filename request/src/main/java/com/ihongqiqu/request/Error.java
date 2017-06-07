package com.ihongqiqu.request;

/**
 * 网络请求失败会调
 * <p>
 * Created by zhenguo on 6/6/17.
 */
public interface Error {

    void onError(int statusCode, String errorMessage, Throwable t);

}
