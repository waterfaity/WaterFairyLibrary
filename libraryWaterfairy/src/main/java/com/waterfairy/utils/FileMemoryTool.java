package com.waterfairy.utils;

import android.os.AsyncTask;

import java.io.File;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019/3/6 10:40
 * @info:
 */
public class FileMemoryTool {
    private OnMemoryCalcListener onMemoryCalcListener;

    public FileMemoryTool() {
    }

    public FileMemoryTool(OnMemoryCalcListener onMemoryCalcListener) {
        this.onMemoryCalcListener = onMemoryCalcListener;
    }


    public void setOnMemoryCalcListener(OnMemoryCalcListener onMemoryCalcListener) {
        this.onMemoryCalcListener = onMemoryCalcListener;
    }

    public void execute(String... filePaths) {
        if (filePaths == null || filePaths.length <= 0) {
            if (onMemoryCalcListener != null) onMemoryCalcListener.onGetMemoryError("文件路径为空!");
        } else {
            File[] files = new File[filePaths.length];
            for (int i = 0; i < filePaths.length; i++) {
                files[i] = new File(filePaths[i]);
            }
            execute(files);
        }
    }

    public void execute(File... files) {
        if (files == null) {
            if (onMemoryCalcListener != null) onMemoryCalcListener.onGetMemoryError("文件为空");
        } else {
            startAsync(files);
        }
    }

    /**
     * @param files
     */
    private synchronized void startAsync(File... files) {
        new AsyncTask<File, Object, Long>() {
            @Override
            protected Long doInBackground(File... files) {
                long totalLength = 0;
                for (File file : files) {
                    totalLength += searchFile(file, totalLength);
                }
                return totalLength;
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                super.onProgressUpdate(values);
                if (onMemoryCalcListener != null) {
                    onMemoryCalcListener.onGetMemoryProgress((String) values[0], (long) (values[1]), (long) values[2]);
                }
            }

            @Override
            protected void onPostExecute(Long length) {
                super.onPostExecute(length);
                if (onMemoryCalcListener != null)
                    onMemoryCalcListener.onGetMemorySuccess(length, getMemorySize(length));
            }

            /**
             * 当前长度
             *
             * @param file        要搜索的文件
             * @param totalLength 已经搜索到的大小
             */
            private long searchFile(File file, long totalLength) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (File tempFile : files) {
                        totalLength += searchFile(tempFile, totalLength);
                    }
                } else {
                    long lengthTemp = file.length();
                    publishProgress(new Object[]{file.getAbsolutePath(), lengthTemp, totalLength});
                    return lengthTemp;
                }
                return totalLength;
            }
        }.execute(files);
    }


    /**
     * @param memorySize KB
     * @return
     */
    public static String getMemorySize(long memorySize) {
        float num = memorySize;
        float tempNum = 0;
        String extension = "B";
        if ((tempNum = num / 1024F) >= 1) {
            num = tempNum;
            if ((tempNum = num / 1024F) >= 1) {
                num = tempNum;
                if ((tempNum = num / 1024F) >= 1) {
                    num = tempNum;
                    if ((tempNum = num / 1024F) >= 1) {
                        num = tempNum;
                        //TB
                        extension = "TB";
                    } else {
                        extension = "GB";
                    }
                } else {
                    extension = "MB";
                }
            } else {
                extension = "KB";
            }
        }
        num = ((int) (num * 100) / 100F);
        return num + extension;
    }


    public interface OnMemoryCalcListener {
        void onGetMemorySuccess(long length, String memoryText);

        void onGetMemoryProgress(String filePath, long length, long totalLength);

        void onGetMemoryError(String msg);
    }
}
