package com.ihongqiqu.request;

/**
 * 网络配置，不涉及网络请求。这里显示提供设置是否显示log
 * <p>
 * Created by zhenguo on 6/7/17.
 */
public class RequestConfig {

    private static boolean showLog = false;

    static boolean isShowLog() {
        return showLog;
    }

    static void showLog() {
        RequestConfig.showLog = true;
    }

    static void hideLog() {
        RequestConfig.showLog = false;
    }

}
