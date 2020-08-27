package com.waterfairy.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import androidx.annotation.NonNull;

/**
 * Created by water_fairy on 2017/3/30.
 * 995637517@qq.com
 */

public class AssetsUtils {
    private static final String TAG = "assetUtils";
//    String path = "file:///android_asset/文件名";
//    InputStream abpath = getClass().getResourceAsStream("/assets/文件名");

    public static String getPath(String filePath) {
        return "file:///android_asset/" + filePath;
    }

    /**
     * @param context
     * @param assetPath  例如html   asset/html  如果  asset
     *                   ath =null  copy所有文件
     * @param targetPath
     */
    public static void copyPath(Context context, String assetPath, @NonNull String targetPath) throws IOException {
        String[] paths = null;
        paths = TextUtils.isEmpty(assetPath) ?
                context.getAssets().getLocales() :
                context.getAssets().list(assetPath);
        copyFile(context, paths, assetPath, targetPath);
    }

    private static void copyFile(Context context, String[] paths, String srcPath, String targetPath) throws IOException {
        if (TextUtils.isEmpty(targetPath)) {
            throw new IOException("targetPath file is null");
        }
        File file = new File(targetPath);
        boolean fileExist = true;
        if (!file.exists()) {
            fileExist = file.mkdirs();
        }
        if (!fileExist) {
            throw new IOException("targetPath file mk error");
        }

        if (paths != null && paths.length != 0) {
            for (String path : paths) {
                String tempSrcPath = srcPath + "/" + path;//asset 中 ,  html + css
                String tempTargetPath = targetPath + "/" + path;//文件中  /html +css
                String[] list = context.getAssets().list(tempSrcPath);
                if (list.length == 0) {
                    //文件
                    copyFile(context, tempSrcPath, tempTargetPath);
                } else {
                    //文件夹
                    copyFile(context, list, tempSrcPath, tempTargetPath);
                }
            }
        } else {
            throw new IOException("no files");
        }
    }

    public static void copyFile(Context context, String tempSrcPath, String tempTargetPath) {
        try {
            InputStream is = context.getAssets().open(tempSrcPath);
            File file = new File(tempTargetPath);
            if (!file.exists()) {
                File parentFile = new File(file.getParent());

                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
            if (file.exists()) {
                OutputStream outputStream = new FileOutputStream(file);
                byte[] buf = new byte[1024 * 512];
                int len;
                while ((len = is.read(buf)) > 0)
                    outputStream.write(buf, 0, len);
                is.close();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context context
     * @param path    assets文件
     * @return
     * @throws IOException
     */

    public static synchronized String getText(Context context, String path) throws IOException {
        InputStream is = getIS(context, path);
        return isToString(is);

    }

    /**
     * @param context context
     * @param path    assets文件
     * @return InputStream
     * @throws IOException
     */
    public static synchronized InputStream getIS(Context context, String path) throws IOException {
        if (context == null) throw new IOException("context is null");
        if (TextUtils.isEmpty(path)) throw new IOException("assets path is null");
        return context.getAssets().open(path);
    }

    /**
     * @param inputStream is
     * @return String
     * @throws IOException
     */
    public static synchronized String isToString(InputStream inputStream) throws IOException {
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(isr);
        String temp = "";
        StringBuilder stringBuffer = new StringBuilder();
        while (!TextUtils.isEmpty(temp = bufferedReader.readLine())) {
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
