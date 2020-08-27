package com.waterfairy.librarymediacache;

import android.content.Context;
import android.text.TextUtils;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019/2/20 15:00
 * @info:
 */
public class CacheProxy {

    public final Config config;

    public CacheProxy(Config config) {
        this.config = config;
    }

    public String generateVideoPath(Context context, String videoPath) {
        //缓存视频
        HttpProxyCacheServer.Builder builder = new HttpProxyCacheServer.Builder(context);
        if (!TextUtils.isEmpty(getCachePath())) {
            builder.cacheDirectory(new File(getCachePath()));
        }
        builder.fileNameGenerator(new Md5FileNameGenerator(isUserExtension()));
        HttpProxyCacheServer httpProxyCacheServer = builder.build();
        return httpProxyCacheServer.getProxyUrl(videoPath);
    }


    public boolean isCacheAble() {
        return config.cacheAble;
    }

    public String getCachePath() {
        return config.cachePath;
    }

    public boolean isUserExtension() {
        return config.userExtension;
    }

    public static class Builder {
        /**
         * 是否可以缓存
         */
        private boolean cacheAble = true;
        private String cachePath;
        private boolean userExtension;


        public Builder cacheAble(boolean cacheAble) {
            this.cacheAble = cacheAble;
            return this;
        }

        public Builder cachePath(String cachePath) {
            this.cachePath = cachePath;
            return this;
        }

        public Builder userExtension(boolean userExtension) {
            this.userExtension = userExtension;
            return this;
        }


        public CacheProxy build() {
            return new CacheProxy(new CacheProxy.Config(cacheAble, cachePath, userExtension));
        }
    }

    private static class Config {
        public boolean cacheAble = true;
        public String cachePath;
        public boolean userExtension;

        public Config(boolean cacheAble, String cachePath, boolean userExtension) {
            this.cacheAble = cacheAble;
            this.cachePath = cachePath;
            this.userExtension = userExtension;
        }
    }
}
