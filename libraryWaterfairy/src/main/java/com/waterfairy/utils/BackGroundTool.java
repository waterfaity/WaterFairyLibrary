package com.waterfairy.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/12/7
 * @Description:
 */

public class BackGroundTool {
    private List<OnBackGroundListener> listeners;
    private List<OnActivityStateChangeListener> stateListeners;
    private List<Activity> activities;
    private static final BackGroundTool BACK_GROUND_TOOL = new BackGroundTool();

    public static final int STATE_CREATE = 1;
    public static final int STATE_DESTROY = 2;

    public static final int STATE_START = 3;
    public static final int STATE_STOP = 4;

    public static final int STATE_RESUME = 5;
    public static final int STATE_PAUSE = 6;

    public static final int STATE_ON_SAVE_INSTANCE = 7;


    private BackGroundTool() {
        listeners = new ArrayList<>();
        stateListeners = new ArrayList<>();
        activities = new ArrayList<>();
    }

    public static BackGroundTool getInstance() {
        return BACK_GROUND_TOOL;
    }

    private int count;

    public void init(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (count == 0) {
                    //切到后台
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).onChangeBackGround(activity, true);
                    }
                }
                onStateChange(STATE_STOP, activity, null);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (count == 0) {
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).onChangeBackGround(activity, false);
                    }
                }
                count++;
                onStateChange(STATE_START, activity, null);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                onStateChange(STATE_ON_SAVE_INSTANCE, activity, outState);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                onStateChange(STATE_RESUME, activity, null);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                onStateChange(STATE_PAUSE, activity, null);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                onStateChange(STATE_DESTROY, activity, null);
                activities.remove(activity);
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                onStateChange(STATE_CREATE, activity, null);
                activities.add(activity);
            }
        });


    }

    public List<Activity> getActivities() {
        return activities;
    }

    private void onStateChange(int state, Activity activity, Bundle bundle) {
        for (int i = 0; i < stateListeners.size(); i++) {
            stateListeners.get(i).onActivityStateChange(state, activity, bundle);
        }
    }

    public void addListener(OnBackGroundListener onBackGroundListener) {
        listeners.add(onBackGroundListener);

    }

    public void addActivityStateListener(OnActivityStateChangeListener onBackGroundListener) {
        stateListeners.add(onBackGroundListener);

    }

    public void closeAllActivity() {
        if (activities != null) {
            for (int i = 0; i < activities.size(); i++) {
                (activities.get(i)).finish();
            }
        }
    }

    public boolean checkActivityAlive(String activityTag) {
        if (activities != null) {
            for (int i = 0; i < activities.size(); i++) {
                String tag = activities.get(i).toString();
                if (TextUtils.equals(activityTag, tag)) return true;
            }
        }
        return false;
    }

    public interface OnBackGroundListener {
        void onChangeBackGround(Activity activity, boolean backGround);
    }

    public interface OnActivityStateChangeListener {
        void onActivityStateChange(int state, Activity activity, Bundle bundle);
    }


}
