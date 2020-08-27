package com.waterfairy.share;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/8/18 17:32
 * @info:
 */
public class PackageInstalledUtils {

    public  static  boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        for (int i = 0; i < packages.size(); i++) {
            String pn = packages.get(i).packageName;
            if (pn.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }
}
