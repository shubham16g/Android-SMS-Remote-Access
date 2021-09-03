package com.acoder.smstrigger;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ApManager {

    //check whether wifi hotspot on or off
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static String onHotspot(Context context) {
        return configApState(context, true);
    }

    public static String offHotspot(Context context) {
        return configApState(context, false);
    }

    // toggle wifi hotspot on or off
    private static String configApState(Context context, boolean requestOn) {
        WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean isApOn = isApOn(context);
        if (isApOn && requestOn) {
            return "Hotspot is already on";
        } else if (!isApOn && !requestOn) {
            return "Hotspot is already off";
        }
        try {
            // if WiFi is on, turn it off
            if (isApOn)
                wifimanager.setWifiEnabled(false);
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, null, !isApOn(context));
            return requestOn ? "Hotspot is turned on successfully" : "Hotspot is turned off successfully";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Sorry! Failed to run the task";
    }

    public static String getStatus(Context context) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float) scale;

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        return "Hotspot: " + (isApOn(context) ? "On" : "Off") + "\n" +
                "Battery: " + batteryPct + "%" + "\n" +
                "Charging: " + (isCharging ? "Yes" : "No");
    }


}
