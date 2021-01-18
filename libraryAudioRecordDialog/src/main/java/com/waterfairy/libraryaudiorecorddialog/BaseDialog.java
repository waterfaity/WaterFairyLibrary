package com.waterfairy.libraryaudiorecorddialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/4/17
 * @Description:
 */

public class BaseDialog extends Dialog implements View.OnClickListener {
    private OnDismissListener onDismissListener;

    protected View mRootView;
    protected Context mContext;
    private Object object;

    public BaseDialog(Context context, int layoutRes) {
        this(context, layoutRes, R.style.recordBaseDialog);
    }

    public BaseDialog(Context context, int layoutRes, int styleRes) {
        super(context, styleRes);
        this.mContext = context;
        mRootView = LayoutInflater.from(context).inflate(layoutRes, null);
        addContentView(mRootView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (mRootView.getId() == R.id.root_view) {
            mRootView.setOnClickListener(this);
        }
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }


    public void setCanClickBGDismiss(boolean canDismiss) {
        if (canDismiss) {
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } else {
            mRootView.setOnClickListener(null);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.root_view) {
            dismiss();
            if (onDismissListener != null) onDismissListener.onDismiss(this);
        }
    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(listener);
        this.onDismissListener = listener;
    }
}