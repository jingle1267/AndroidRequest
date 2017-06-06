package com.ihongqiqu.request.cache;

import android.content.Context;
import okhttp3.Cache;

/**
 * 网络缓存
 * <p>
 * Created by zhenguo on 6/6/17.
 */
public class CacheProvide {
    Context mContext;

    public CacheProvide(Context context) {
        mContext = context;
    }

    public Cache provideCache() {
        return new Cache(mContext.getCacheDir(), 50*1024 * 1024);
    }
}
