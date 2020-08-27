package com.waterfairy.share;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/8/18 17:10
 * @info:
 */
public class QQShareTool {
    public static String APP_ID = "1106115038";
    private Tencent mTencent;

    public static QQShareTool newInstance() {
        return new QQShareTool();
    }

    public void register(Context context) {
        mTencent = Tencent.createInstance(APP_ID, context);
    }


    public static Bundle geneBundle(String title, String desc, String url, String imgUrl) {
        Bundle params = new Bundle();
        if (!TextUtils.isEmpty(url))
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, desc);
        if (!TextUtils.isEmpty(url))
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        if (!TextUtils.isEmpty(imgUrl))
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
        return params;
    }

    public void send(Activity activity, boolean isZone, Bundle params) {

        if (isZone) {
            mTencent.shareToQzone(activity, params, getListener());
        } else {
            mTencent.shareToQQ(activity, params, getListener());
        }
    }

    private IUiListener getListener() {
        return new IUiListener() {
            @Override
            public void onComplete(Object o) {

            }

            @Override
            public void onError(UiError uiError) {
                Log.i("TAG", "onError: " + uiError.errorMessage + " " + uiError.errorDetail);
            }

            @Override
            public void onCancel() {

            }
        };
    }
}
