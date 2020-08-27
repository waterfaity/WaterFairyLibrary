package com.waterfairy.downloader.down;

import com.waterfairy.downloader.base.BaseBeanInfo;
import com.waterfairy.downloader.base.ProgressListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by water_fairy on 2017/4/26.
 */

public class DownloadInterceptor<T extends BaseBeanInfo> implements Interceptor {
    private ProgressListener<T> downloadingListener;
    private T baseBeanInfo;

    public DownloadInterceptor(T beanInfo, ProgressListener<T> downloadingListener) {
        this.baseBeanInfo = beanInfo;
        this.downloadingListener = downloadingListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response oriResponse = chain.proceed(chain.request());
        return oriResponse.newBuilder()
                .body(new DownloadResponseBody(oriResponse.body(), baseBeanInfo, oriResponse.code(), downloadingListener))
                .build();
    }
}
