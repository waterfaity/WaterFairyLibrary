package me.goldze.mvvmhabit.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/7/23 10:21
 * @info:
 */
public class DateTool {
    public static long lastNetTime = System.currentTimeMillis();
    public static long lastSysTime = lastNetTime;

    /**
     * 同步网络时间
     *
     * @param netTime
     */
    public static void initTime(String netTime) {
        if (!TextUtils.isEmpty(netTime) && netTime.length() == 19) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date parse = simpleDateFormat.parse(netTime);
                lastNetTime = parse.getTime();
                lastSysTime = System.currentTimeMillis();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static long getNetTime() {
        return (System.currentTimeMillis() - lastSysTime) + lastNetTime;
    }

    public static String getLeaveTime(String endTime, long extraTime) {
        if (!TextUtils.isEmpty(endTime) && endTime.length() == 19) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date parse = simpleDateFormat.parse(endTime);
                Log.i("TAG", "getLeaveTime: end:" + endTime + " \t net:" + simpleDateFormat.format(getNetTime()) + "  dTime: " + (parse.getTime() + extraTime - getNetTime()));
                long leaveTime = parse.getTime() + extraTime - getNetTime();
                if (leaveTime <= 0) return "";
                return transLeaveTime(parse.getTime() + extraTime - getNetTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private static String transLeaveTime(long time) {
        long second = time / 1000 % 60;
        long minus = time / (60 * 1000) % 60;
        long hour = time / (60 * 60 * 1000) % 24;
        long day = time / (24 * 60 * 60 * 1000);
        return day != 0 ? (day + "天") : ((hour == 0 ? (minus + "分钟") : (hour + "小时")));
    }

    /**
     * 刚刚 20分钟前 12小时前
     * //小于1分钟
     * //小于1天
     * 其他 显示具体时间
     *
     * @param time
     * @return
     */
    public static String getUseTime(String time) {
        if (!TextUtils.isEmpty(time) && time.length() == 19) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date parse = simpleDateFormat.parse(time);
                long userTime = getNetTime() - parse.getTime();
                if (userTime < 60 * 1000) {
                    //小于1分钟
                    return "刚刚";
                } else if (userTime < 24 * 60 * 60 * 1000) {
                    //小于1天
                    return transLeaveTime(userTime) + "前";
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return time;
    }
}
