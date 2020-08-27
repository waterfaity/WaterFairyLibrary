package com.waterfairy.downloader.base;


import com.waterfairy.downloader.down.DownloadInterceptor;
import com.waterfairy.downloader.down.DownloadService;
import com.waterfairy.downloader.upload.UploadService;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by water_fairy on 2017/5/18.
 * 995637517@qq.com
 */

public class RetrofitRequest<T extends BaseBeanInfo> {
    private static RetrofitRequest retrofitRequest;
    private UploadService uploadRetrofitService;
    private String baseUrl;

    private RetrofitRequest() {

    }

    public static RetrofitRequest getInstance() {
        if (retrofitRequest == null) retrofitRequest = new RetrofitRequest();
        return retrofitRequest;
    }

    public void initBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public UploadService getUploadRetrofit() {
        if (uploadRetrofitService == null) {
            uploadRetrofitService = buildClient(null).create(UploadService.class);
        }
        return uploadRetrofitService;
    }

    public DownloadService getDownloadRetrofit(T beanInfo, ProgressListener<T> progressListener) {
        return buildClient(new DownloadInterceptor<>(beanInfo, progressListener)).create(DownloadService.class);
    }

    private Retrofit buildClient(DownloadInterceptor<T> downloadInterceptor) {
        OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder();
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) {

                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {

                }
            }};


            HostnameVerifier hostnameVerifier = (hostname, session) -> true;
            okHttpClient.hostnameVerifier(hostnameVerifier);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            okHttpClient.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        okHttpClient.connectTimeout(15000, TimeUnit.MILLISECONDS);
        okHttpClient.readTimeout(15000, TimeUnit.MILLISECONDS);
        if (downloadInterceptor != null)
            okHttpClient.addInterceptor(downloadInterceptor);
        return new Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient.build()).build();
    }
}
