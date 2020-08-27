package com.waterfairy.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import androidx.core.app.ActivityCompat;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019-11-15 13:47
 * @info:
 */
public class LocationTool {
    public static final int REQUEST_PERMISSION = 10010;

    public static final String LOCATION_PERMISSIONS[] = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final int ERROR_NO_PERMISSION = 1;//没有权限
    public static final int ERROR_ACTIVITY_IS_EMPTY = 2;//页面已销毁
    public static final int ERROR_NO_PROVIDER_CAN_USE = 3;//没有位置获取途径
    public static final int ERROR_WAITING_FRESH = 4;//等待刷新
    public static final int ERROR_LOCATION_DISABLE = 5;//位置定位未打开

    private static final String TAG = "location";
    private OnGetLocationListener onGetLocationListener;
    private LocationManager locationManager;
    private HashMap<String, LocationListener> listenerHashMap;
    //    private LocationListener noListener;
    private Activity activity;

    public LocationTool setOnGetLocationListener(OnGetLocationListener onGetLocationListener) {
        this.onGetLocationListener = onGetLocationListener;
        return this;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (permissions != null) {
                boolean hasPermission = true;
                for (String permissionTemp : LOCATION_PERMISSIONS) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (TextUtils.equals(permissionTemp, permissions[i]) && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            hasPermission = false;
                        }
                    }
                }
                if (hasPermission) {
                    location(activity);
                }
            }
        }
    }

    public interface OnGetLocationListener {
        void onGetLocation(Location location);

        void onGetLocationError(int errCode, String msg);
    }

    private LocationTool() {

    }

    public static LocationTool newInstance() {
        return new LocationTool();
    }

    /**
     * 定位
     *
     * @param activity
     * @return
     */
    public void location(Activity activity) {
        if (activity != null) {
            this.activity = activity;
            //请求权限
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, LOCATION_PERMISSIONS, REQUEST_PERMISSION);
                onGetLocationListener.onGetLocationError(ERROR_NO_PERMISSION, "没有权限");
            } else {
                if (locationManager == null)
                    locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

                releaseListener();
                //获取最优provide
                String provider = locationManager.getBestProvider(getCriteria(), true);

                if (!TextUtils.isEmpty(provider)) {
                    Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
                    if (lastKnownLocation == null) {
                        List<String> allProviders = locationManager.getAllProviders();
                        for (String providerTemp : allProviders) {
                            Location lastKnownLocationTemp = locationManager.getLastKnownLocation(providerTemp);
                            if (lastKnownLocationTemp != null) {
                                lastKnownLocation = lastKnownLocationTemp;
                                break;
                            }
                        }
                    }
                    if (lastKnownLocation != null) {
                        if (onGetLocationListener != null) {
                            onGetLocationListener.onGetLocation(lastKnownLocation);
                        }
                    } else {
                        onGetLocationListener.onGetLocationError(ERROR_WAITING_FRESH, "等待刷新");
                        if (listenerHashMap == null) {
                            listenerHashMap = new HashMap<>();
                        }
                        List<String> allProviders = locationManager.getAllProviders();
                        for (String providerTemp : allProviders) {
                            Log.i(TAG, "location: requestLocationUpdates " + providerTemp);
                            LocationListener updateListener = getLocationUpdateListener();
                            listenerHashMap.put(providerTemp, updateListener);
                            locationManager.requestLocationUpdates(providerTemp, 1000, 0, updateListener);
                        }
                    }
                } else {
                    onGetLocationListener.onGetLocationError(ERROR_NO_PROVIDER_CAN_USE, "位置信息获取途径不可用");
                }
            }
        } else {
            onGetLocationListener.onGetLocationError(ERROR_ACTIVITY_IS_EMPTY, "没有权限");
        }
    }

    private void releaseListener() {
        if (listenerHashMap != null && locationManager != null) {
            Set<String> strings = listenerHashMap.keySet();
            for (String provider : strings) {
                locationManager.removeUpdates(listenerHashMap.get(provider));
            }
            listenerHashMap.clear();
        }
    }

    public void release() {
        if (locationManager != null) {
            releaseListener();
            locationManager = null;
            activity = null;
        }
    }

    private LocationListener getLocationUpdateListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (listenerHashMap != null) {
                    LocationListener locationListener = listenerHashMap.get(location.getProvider());
                    if (locationListener != null) {
                        locationManager.removeUpdates(locationListener);
                        listenerHashMap.remove(location.getProvider());
                        if (onGetLocationListener != null && activity != null) {
                            onGetLocationListener.onGetLocation(location);
                        }
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                if (onGetLocationListener != null) {
                    onGetLocationListener.onGetLocationError(ERROR_LOCATION_DISABLE, "定位未打开");
                }
            }
        };
    }

    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        return criteria;
    }
}
