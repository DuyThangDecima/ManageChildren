package com.thangld.managechildren.cloud.resource;

import android.content.Context;
import android.util.Log;

import com.thangld.managechildren.cloud.HttpConnection;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.utils.DeviceInfoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by thangld on 29/03/2017.
 */

public class AccountResource {

    /**
     * Xóa dữ liệu
     *
     * @param context
     */
    public static void setLogout(Context context) {
        Log.d("mc_log","setLogout" + context.getClass());
        // Xóa token
        PreferencesController preferences = new PreferencesController(context);
        preferences.remove(PreferencesController.TOKEN_KEY);
        // set statuslogin
        preferences.putStatusLogin(false);
        // Set privilege unknow
        preferences.putPrivilege(PreferencesController.PRIVILEGE_UNKNOWN);
        // Xoa child_id
        preferences.remove(PreferencesController.CHILD_ID_KEY);
    }


    public static String syncAccount(JSONObject jsonObject) throws MalformedURLException {

        URL url = new URL(UrlPattern.HOST + UrlPattern.API_VERSION + UrlPattern.SYNC_ACCOUNT);
        String respond = HttpConnection.exePostConnection(url, jsonObject);

        return respond;
    }

    /**
     * Đăng nhập hệ thống
     *
     * @return
     */
    public static String login(JSONObject jsonObject) throws MalformedURLException {
        URL url = new URL(UrlPattern.HOST + UrlPattern.API_VERSION + UrlPattern.AUTHENTICATION_RESOURCE);
        String respond = HttpConnection.exePostConnection(url, jsonObject);
        return respond;
    }

    public static String signUp(JSONObject jsonObject) throws MalformedURLException {
        URL url = new URL(UrlPattern.HOST + UrlPattern.API_VERSION + UrlPattern.PARENT_RESOURCE);
        String respond = HttpConnection.exePostConnection(url, jsonObject);
        return respond;
    }


    public static String getLatestDeviceChild(Context context, String child_id) {
        String respond = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(UrlPattern.CHILD_ID_KEY, child_id);
            jsonObject.put(UrlPattern.IMEI_KEY, DeviceInfoUtils.getImei(context));
            jsonObject.put(UrlPattern.DEVICE_NAME_KEY, DeviceInfoUtils.getDeviceName());
            jsonObject.put(UrlPattern.TOKEN_KEY, new PreferencesController(context).getToken());
            String url = UrlPattern.HOST + UrlPattern.API_VERSION + UrlPattern.GET_LATEST_DEVICE_CHILD;
            respond = HttpConnection.exePostConnection(new URL(url), jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return respond;
    }

}
