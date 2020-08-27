package com.waterfairy.update;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.waterfairy.utils.BackGroundTool;
import com.waterfairy.utils.ProviderUtils;

import java.io.File;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/8/12 13:36
 * @info:
 */
public class PackageInstall {
    public static void installApk(Context context, String apkFile) {

        File file = new File(apkFile);
        if (!file.exists() || file.length() <= 0) {
            Toast.makeText(context, "安装文件不存在", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri contentUri = ProviderUtils.getProviderUri(context, intent, file);
            intent.setDataAndType(contentUri,
                    "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            BackGroundTool.getInstance().closeAllActivity();
        }
    }
}
