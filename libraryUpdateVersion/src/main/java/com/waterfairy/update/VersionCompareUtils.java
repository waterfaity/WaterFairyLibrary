package com.waterfairy.update;

import android.text.TextUtils;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/8/12 13:35
 * @info:
 */
public class VersionCompareUtils {
    public static boolean compareVersion(String localVer, String netVersion) {

        boolean canUpdate = false;

        localVer = localVer.replace(".", "-");
        netVersion = netVersion.replace(".", "-");

        String[] compatibleVersions = netVersion.split("-");
        String[] localVersions = localVer.split("-");

        for (int i = 0; i < compatibleVersions.length; i++) {
            String netTemp = compatibleVersions[i];
            String localTemp = i >= localVersions.length ? "0" : localVersions[i];
            int netInt = 0;
            int localInt = 0;
            if (!TextUtils.isEmpty(netTemp)) {
                netInt = Integer.parseInt(netTemp);//
            } else {
                netInt = 0;
            }
            if (!TextUtils.isEmpty(localTemp)) {
                localInt = Integer.parseInt(localTemp);
            } else {
                localInt = 0;
            }
            if (netInt > localInt) {
                //服务器版本
                canUpdate = true;
                break;
            } else if (netInt < localInt) {
                //本地版本高
                canUpdate = false;
                break;
            }
        }
        return canUpdate;
    }
}
