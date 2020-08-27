package com.waterfairy.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by water_fairy on 2016/11/30.
 * Update by water_fairy on 2017/10/12.
 */

public class ToastUtils {
    private static Toast mToast;
    private static Context mApplicationContext;

    public static void initToast(Context context) {
        mApplicationContext = context;
    }


    /**
     * 默认show
     */
    public static void show(int resId) {
        show(mApplicationContext.getString(resId));
    }

    public static void show(String content) {
        cancel();
        mToast = Toast.makeText(mApplicationContext, content, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * 取消显示
     */
    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}
