package com.waterfairy.downloader.upload;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.waterfairy.downloader.base.BaseBeanInfo;
import com.waterfairy.downloader.base.ProgressBean;
import com.waterfairy.downloader.base.ProgressListener;
import com.waterfairy.downloader.base.RetrofitRequest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019/4/4 16:11
 * @info: 上传异步处理
 */
public class UploadTask<T extends BaseBeanInfo> extends AsyncTask<T, ProgressBean<T>, ProgressBean<T>> {
    private Call<ResponseBody> call;
    private T beanInfo;
    private OnUploadListener<T> onUploadListener;
    private boolean isExecuted;//是否已经执行
    private boolean isPaused;//暂停状态

    public UploadTask<T> setOnUploadListener(OnUploadListener<T> onUploadListener) {
        this.onUploadListener = onUploadListener;
        return this;
    }

    public UploadTask(T beanInfo) {
        this.beanInfo = beanInfo;
    }

    public T getBeanInfo() {
        return beanInfo;
    }

    @Override
    protected ProgressBean<T> doInBackground(T... beanInfos) {
        ProgressBean<T> progressBean;
        beanInfo.setState(BaseBeanInfo.STATE_LOADING);

        //retrofitService
        UploadService uploadService = RetrofitRequest.getInstance().getUploadRetrofit();

        //添加file
        if (!TextUtils.isEmpty(beanInfo.getFilePath())) {
            File file = new File(beanInfo.getFilePath());
            if (file.exists() && file.canRead()) {
                RequestBody sourceBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                UploadRequestBody<T> uploadRequestBody = new UploadRequestBody<T>(sourceBody, beanInfo, new ProgressListener<T>() {
                    @Override
                    public void onProgressing(T beanInfo, long total, long current) {
                        //上传进度
                        publishProgress(new ProgressBean<T>(ProgressBean.STATE_PROGRESS, beanInfo));
                    }
                });
                MultipartBody.Part filePart;
                try {
                    filePart = MultipartBody.Part.createFormData(
                            "file",
                            URLEncoder.encode(file.getName(), "utf-8"),
                            uploadRequestBody);

                    //添加参数 (如果paramsHashMap 不为null)
                    HashMap<String, String> paramsHashMap = beanInfo.getParamsHashMap();

                    if (beanInfo.getCurrentLength() != 0) {
                        if (paramsHashMap != null) {
                            call = uploadService.upload(beanInfo.getUploadUrl(), "bytes=" + beanInfo.getCurrentLength() + "-" + beanInfo.getTotalLength(), paramsHashMap, filePart);
                        } else {
                            call = uploadService.upload(beanInfo.getUploadUrl(), "bytes=" + beanInfo.getCurrentLength() + "-" + beanInfo.getTotalLength(), filePart);
                        }
                    } else {
                        if (paramsHashMap != null) {
                            call = uploadService.upload(beanInfo.getUploadUrl(), paramsHashMap, filePart);
                        } else {
                            call = uploadService.upload(beanInfo.getUploadUrl(), filePart);
                        }
                    }
                    try {
                        //启动下载
                        Response<ResponseBody> response = call.execute();
                        //下载结束
                        if (response.code() == 200 && response.body() != null) {
                            progressBean = new ProgressBean<>(ProgressBean.STATE_RESULT, beanInfo).setResultCode(response.code()).setResultData(response.body().string());
                        } else {
                            progressBean = new ProgressBean<>(ProgressBean.STATE_RESULT, beanInfo).setResultCode(BaseBeanInfo.ERROR_CODE).setResultData("未知异常");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        //下载异常
                        if (isPaused) {
                            progressBean = new ProgressBean<>(ProgressBean.STATE_PAUSED, beanInfo).setResultCode(BaseBeanInfo.ERROR_CODE).setResultData("暂停下载");
                        } else {
                            progressBean = new ProgressBean<>(ProgressBean.STATE_RESULT, beanInfo).setResultCode(BaseBeanInfo.ERROR_CODE).setResultData("未知异常");
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    progressBean = new ProgressBean<>(ProgressBean.STATE_RESULT, beanInfo).setResultCode(BaseBeanInfo.ERROR_CODE).setResultData("文件名转码错误");
                }
            } else {
                progressBean = new ProgressBean<>(ProgressBean.STATE_RESULT, beanInfo).setResultCode(BaseBeanInfo.ERROR_CODE).setResultData("文件读取失败");
            }
        } else {
            progressBean = new ProgressBean<>(ProgressBean.STATE_RESULT, beanInfo).setResultCode(BaseBeanInfo.ERROR_CODE).setResultData("文件地址为空");
        }
        return progressBean;
    }

    @Override
    protected void onPostExecute(ProgressBean<T> progressBean) {
        super.onPostExecute(progressBean);

        if (onUploadListener != null) {
            //下载结束
            if (progressBean.getResultCode() != 200) {
                //下载失败  1:暂停;2:失败
                if (progressBean.getState() == ProgressBean.STATE_PAUSED) {
                    progressBean.getBeanInfo().setState(BaseBeanInfo.STATE_PAUSED);
                    onUploadListener.onUploadPaused(beanInfo);
                } else {
                    progressBean.getBeanInfo().setState(BaseBeanInfo.STATE_ERROR);
                    onUploadListener.onUploadError(beanInfo, progressBean.getResultData());
                }
            } else {
                //下载成功
                onUploadListener.onUploadSuccess(beanInfo, progressBean.getResultData());
            }
        }
    }

    @Override
    protected void onProgressUpdate(ProgressBean<T>... values) {
        super.onProgressUpdate(values);
        ProgressBean<T> beanInfo = values[0];
        if (onUploadListener != null) {
            if (beanInfo.getState() == ProgressBean.STATE_PROGRESS) {
                //下载进度
                onUploadListener.onUploadProgress(beanInfo.getBeanInfo());
            } else if (beanInfo.getState() == ProgressBean.STATE_START) {
                //下载开始
                onUploadListener.onUploadStart(beanInfo.getBeanInfo());
            }
        }
    }


    /**
     * 并发线程
     */
    public void execute() {
        isPaused = false;
        if (isExecuted) return;
        isExecuted = true;
        //并发线程
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public boolean isExecuted() {
        return isExecuted;
    }

    /**
     * 暂停
     *
     * @param checkState
     * @return
     */
    public boolean pause(boolean checkState) {
        if (!checkState//不执行检查状体
                || beanInfo.getState() == BaseBeanInfo.STATE_WAITING//执行检查状态 等待中
                || beanInfo.getState() == BaseBeanInfo.STATE_START//执行检查状态 开始
                || beanInfo.getState() == BaseBeanInfo.STATE_LOADING) {//执行检查状态 传输中

            //已经暂停 返回
            if (isPaused) return false;
            isPaused = true;
            //还未开始 返回
            if (!isExecuted) return false;
            //已经开始 取消任务
            return cancel();
        }
        return false;
    }

    /**
     * 暂停
     */
    public boolean pause() {
        return pause(false);
    }

    /**
     * 取消 停止上传
     */
    public boolean cancel() {
        if (call != null && isExecuted) {
            call.cancel();
            return true;
        } else {
            return false;
        }
    }


    public interface OnUploadListener<T extends BaseBeanInfo> {
        void onUploadStart(T beanInfo);

        void onUploadPaused(T beanInfo);

        void onUploadProgress(T beanInfo);

        void onUploadSuccess(T beanInfo, String jsonResult);

        void onUploadError(T beanInfo, String errMsg);
    }

}
