package com.ihongqiqu.request;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局参数
 * <p>
 * Created by zhenguo on 6/6/17.
 */
public class GlobalParams {

    private static Map<String, String> commonParams = new HashMap<>();

    private static Map<String, String> commonHeaders = new HashMap<>();

    public static Map<String, String> getCommonParams() {
        return commonParams;
    }

    public static void setCommonParams(Map<String, String> commonParams) {
        GlobalParams.commonParams = commonParams;
    }

    public static Map<String, String> getCommonHeaders() {
        return commonHeaders;
    }

    public static void setCommonHeaders(Map<String, String> commonHeaders) {
        GlobalParams.commonHeaders = commonHeaders;
    }
}
