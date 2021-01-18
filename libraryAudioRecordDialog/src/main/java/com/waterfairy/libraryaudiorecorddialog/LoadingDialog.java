package com.waterfairy.libraryaudiorecorddialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/4/25
 * @Description:
 */
public class LoadingDialog extends BaseDialog {
    private TextView mTVContent;

    public LoadingDialog(Context context, String content) {
        super(context, R.layout.dialog_record_loading, R.style.dialogTransBg);

        mTVContent = findViewById(R.id.content);
        mTVContent.setVisibility(View.GONE);
        setContent(content);
    }

    public void setContent(String content) {
        if (mTVContent != null) {
            if (TextUtils.isEmpty(content)) {
                mTVContent.setVisibility(View.GONE);
            } else {
                mTVContent.setVisibility(View.VISIBLE);
            }
            mTVContent.setText(content);
        }
    }
}
