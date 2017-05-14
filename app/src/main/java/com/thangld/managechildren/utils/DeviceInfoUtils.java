package com.thangld.managechildren.utils;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

/**
 * Created by thangld on 05/04/2017.
 */

public class DeviceInfoUtils {

    private static final int MY_REQUEST_READ_PHONE_STATE = 1;

    public static String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (manufacturer == null) {
            manufacturer = "";
        }
        if (model == null) {
            manufacturer = "";
        }
        if (manufacturer == "" && model == "") {
            return "unknown_device";
        } else if (manufacturer == "" && model != "") {
            return model;
        } else if (model == "" && manufacturer != "") {
            return manufacturer;
        } else {
            return manufacturer + "-" + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
