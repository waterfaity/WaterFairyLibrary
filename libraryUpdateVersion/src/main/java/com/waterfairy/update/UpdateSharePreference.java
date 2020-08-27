package com.waterfairy.update;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/8/12 09:54
 * @info:
 */
public class UpdateSharePreference {
    private final SharedPreferences appUpdate;

    public UpdateSharePreference(SharedPreferences appUpdate) {
        this.appUpdate = appUpdate;
    }

    public String getIgnoreVersion() {
        return appUpdate.getString("ignore_version", "1.0.0");
    }

    public void saveIgnoreVersion(String version) {
        appUpdate.edit().putString("ignore_version", version).apply();
    }

    public static UpdateSharePreference newInstance(Context context) {
        return new UpdateSharePreference(context.getSharedPreferences("app_update", Activity.MODE_PRIVATE));
    }
}
