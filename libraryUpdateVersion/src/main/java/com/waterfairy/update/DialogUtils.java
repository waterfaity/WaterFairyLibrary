package com.waterfairy.update;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/8/12 10:53
 * @info:
 */
public class DialogUtils {
    public static ProgressDialog getProgressDialog(Context context) {
        //下载dialog
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMax(100);
        progressDialog.setTitle("下载中");
        progressDialog.setCancelable(false);
        progressDialog.setProgress(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        return progressDialog;
    }
}
