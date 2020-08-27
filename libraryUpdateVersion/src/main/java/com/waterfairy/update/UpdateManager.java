package com.waterfairy.update;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.waterfairy.downloader.base.BaseBeanInfo;
import com.waterfairy.downloader.down.DownloadTool;
import com.waterfairy.utils.BackGroundTool;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/8/12 09:18
 * @info:
 */
public class UpdateManager {
    private static UpdateManager updateManager;
    private List<SoftReference<UpdateListener>> updateListeners;
    private String localVersion = "";
    private UpdateSharePreference updateSharePreference;
    private boolean isDownloading;
    private DownloadTool downloadTool;
    private UpdateBean updateBean;
    private Context context;
    private ProgressDialog progressDialog;

    public static UpdateManager getInstance() {
        if (updateManager == null) updateManager = new UpdateManager();
        return updateManager;
    }


    /**
     * @param context
     * @param updateBean
     * @param ignoreIgnore 无视忽略
     */
    public boolean check(Context context, UpdateBean updateBean, boolean ignoreIgnore) {
        initLocalVersion(context);
        updateSharePreference = UpdateSharePreference.newInstance(context);
        this.context = context;
        this.updateBean = updateBean;
        //可以更新
        if (VersionCompareUtils.compareVersion(localVersion, updateBean.getAppVersion())) {
            //设置保存路径
            if (TextUtils.isEmpty(updateBean.getSavePath())) {
                File file = new File(new File(context.getExternalCacheDir(), "apk"), updateBean.getAppPackage() + "_" + updateBean.getAppVersion() + ".apk");
                updateBean.setSavePath(file.getAbsolutePath());
            }

            if (updateBean.getForceUpgrade()) {
                //强制更新 -> 弹窗
                showDialog();
            } else {
                //版本忽略版本
                String ignoreVersion = updateSharePreference.getIgnoreVersion();
                //是否被忽略
                boolean isIgnore = TextUtils.equals(ignoreVersion, updateBean.getAppVersion());
                //是否无视忽略
                if (!isIgnore || ignoreIgnore) {
                    showDialog();
                }
            }
            return true;
        } else {
            //没有更新
            sendNoUpdateMsg();
        }
        return false;
    }


    /**
     * 获取当前程序版本
     *
     * @param context
     * @return
     */
    private void initLocalVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("发现新版本");
        builder.setMessage("版本号:" + updateBean.getAppVersion() + "\n" + "更新内容:" + updateBean.getAppDesc());
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoad();
            }
        });
        if (!updateBean.getForceUpgrade()) {
            builder.setNegativeButton("忽略该版本", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateSharePreference.saveIgnoreVersion(updateBean.getAppVersion());
                    sendIgnoreMsg();
                }
            });
        }
        builder.create().show();
    }

    private void downLoad() {
        progressDialog = DialogUtils.getProgressDialog(context);
        isDownloading = true;
        if (downloadTool == null) {
            downloadTool = new DownloadTool();
            downloadTool.setDownloadListener(new DownloadTool.OnDownloadListener() {
                @Override
                public void onDownloadStart(BaseBeanInfo mediaResBean) {

                }

                @Override
                public void onDownloading(BaseBeanInfo mediaResBean) {
                    progressDialog.setProgress(mediaResBean.getProgressRate());
                }

                @Override
                public void onDownloadPaused(BaseBeanInfo beanInfo) {

                }

                @Override
                public void onDownloadSuccess(BaseBeanInfo mediaResBean) {
                    progressDialog.dismiss();
                    PackageInstall.installApk(context, updateBean.getSavePath());
                }

                @Override
                public void onDownloadError(BaseBeanInfo mediaResBean) {
                    isDownloading = false;
                    progressDialog.dismiss();
                    showErrorDialog();
                }

                @Override
                public void onDownloadAll() {

                }
            });
        }
        BaseBeanInfo beanInfo = downloadTool.getBeanInfo(updateBean.getAppUrl());
        if (beanInfo == null) {
            //开始下载
            progressDialog.show();
            BaseBeanInfo baseBeanInfo = new BaseBeanInfo();
            baseBeanInfo.setUrl(updateBean.getAppUrl()).setFilePath(updateBean.getSavePath());
            downloadTool.addDownload(baseBeanInfo);
            downloadTool.start();
        } else {
            if (beanInfo.getState() == BaseBeanInfo.STATE_SUCCESS) {
                PackageInstall.installApk(context, updateBean.getSavePath());
            } else {
                progressDialog.show();
                //重新下载/继续下载
                downloadTool.restart(updateBean.getAppUrl());
            }
        }
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("下载失败,是否继续?");
        builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                downLoad();
            }
        });
        builder.setNegativeButton(updateBean.getForceUpgrade() ? "退出" : "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (updateBean.getForceUpgrade()) {
                    BackGroundTool.getInstance().closeAllActivity();
                }
            }
        });

        builder.setCancelable(!updateBean.getForceUpgrade());
        builder.create().show();
    }

    private void sendNoUpdateMsg() {
        if (updateListeners != null) {
            for (int i = 0; i < updateListeners.size(); i++) {
                if (updateListeners.get(i) != null && updateListeners.get(i).get() != null) {
                    updateListeners.get(i).get().onNoUpdate();
                }
            }
        }
    }

    private void sendIgnoreMsg() {
        if (updateListeners != null) {
            for (int i = 0; i < updateListeners.size(); i++) {
                if (updateListeners.get(i) != null && updateListeners.get(i).get() != null) {
                    updateListeners.get(i).get().onIgnore();
                }
            }
        }
    }

    public void addUpdateListener(UpdateListener updateListener) {
        if (updateListener != null) {
            if (updateListeners == null) updateListeners = new ArrayList<>();
            for (int i = 0; i < updateListeners.size(); i++) {
                if (updateListeners.get(i) != null && updateListener == updateListeners.get(i).get()) {
                    return;
                }
            }
            updateListeners.add(new SoftReference<>(updateListener));
        }
    }

    public void removeUpdateListener(UpdateListener updateListener) {
        if (updateListener != null && updateListeners != null) {
            for (int i = 0; i < updateListeners.size(); i++) {
                if (updateListeners.get(i) != null && updateListener == updateListeners.get(i).get()) {
                    updateListeners.remove(i);
                    return;
                }
            }
        }
    }

}
