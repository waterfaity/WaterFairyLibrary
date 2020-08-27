package com.waterfairy.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/1/25
 * @Description: 注意:smallIcon 设置为图片文件  , 26之后会使用xml文件  报错
 * 8.0
 * 创建 channelId 之后才可以 使用通知 (createNotificationChannel)
 */

public class NotificationUtils extends ContextWrapper {
    private NotificationManager manager;
    public static final String channelId = "channel_1";
    public static final String channelName = "内部通知";

    public NotificationUtils(Context context) {
        super(context);
    }

    /**
     * 获取通知管理类
     *
     * @return NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    /**
     * 发送通知
     *
     * @param smallIcon
     * @param title
     * @param content
     */
    public Notification.Builder getNotificationBuilder(int smallIcon, String title, String content) {
        return getNotificationBuilder(smallIcon, title, content, channelId, channelName);
    }

    /**
     * 发送通知
     *
     * @param smallIcon
     * @param title
     * @param content
     * @param channelId
     * @param channelName
     * @return
     */
    public Notification.Builder getNotificationBuilder(int smallIcon, String title, String content, String channelId, String channelName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId, channelName);
            return getChannelNotificationBuilder(channelId, smallIcon, title, content);
        } else {
            return getNotificationBuilderNormal(smallIcon, title, content);
        }
    }

    /**
     * @param notification
     */
    public void sendNotification(Notification notification) {
        getManager().notify(1, notification);
    }

    public void sendNotification(int smallIcon, String title, String content) {
        sendNotification(getNotificationBuilder(smallIcon, title, content).build());
    }

    /**
     * 8.0及以上需要创建channel
     * 创建channel
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel(String channelId, String channelName) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    /**
     * 通知之前调用 createNotificationChannel
     * 获取channel(26及以上) notification.builder
     *
     * @param channelId
     * @param smallIcon
     * @param title
     * @param content
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotificationBuilder(String channelId, int smallIcon, String title, String content) {
        return new Notification.Builder(getApplicationContext(), channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true);
    }

    /**
     * 获取channel(26以下) notification.builder
     *
     * @param smallIcon
     * @param title
     * @param content
     * @return
     */
    public Notification.Builder getNotificationBuilderNormal(int smallIcon, String title, String content) {
        return new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true);
    }
}
