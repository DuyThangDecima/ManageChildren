package com.thangld.managechildren.storage.controller;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by thangld on 09/04/2017.
 */

public class PreferencesController {
    private String myPreferences = "my_preferences";

    private SharedPreferences sharedPreferences;
    private Context context;

    /**
     * Lưu token của app
     */
    public static final String TOKEN_KEY = "token";

    /**
     * Childid
     */

    public static final String CHILD_ID_KEY = "child_id";

    /**
     * 2 trạng thái on or off
     * on: Cho phép app hoạt động
     * off: App dừng hoạt động, không thực hiện bất kỳ hoạt động nào
     */
    private static final String STATUS_APP_KEY = "status_app";
    /**
     * true, false
     */
    private static final String STATUS_LOGIN_KEY = "status_login";

    /**
     *
     */
    public static final String PRIVILEGE_KEY = "privilege";
    public static final String PRIVILEGE_PARENT = "privilege_parent";
    public static final String PRIVILEGE_CHILD = "privilege_child";
    public static final String PRIVILEGE_UNKNOWN = "privilege_unknown";

    public static final String IMEI_KEY = "imei";
    public static final String DEVICE_NAME_KEY = "device_name";
    public static final String LATEST_READER_KEY = "latest_reader";
    public static final String LATEST_DEVICE_CHILD = "latest_device_child";
    public static final String LATEST_DOWNLOAD_CONTACT_KEY = "latest_download_contact";


    public static final String LATEST_TRANSFER_KEY = "latest_transfer";
    public static final String REQUEST_SYNC_ACCOUNT_KEY = "request_sync_account";


    public static final String TIME_USE_IN_DAY = "time_use_in_day";

    public static final String PASSWORD_CHILD = "password_child";


    public PreferencesController(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).commit();
    }

    /**
     * Token app
     *
     * @param token
     */
    public void putToken(String token) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).commit();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    /***
     * Trạng thái của app,
     * có được active hay không
     * @param token
     */
    public void putStatusApp(boolean token) {
        sharedPreferences.edit().putBoolean(STATUS_APP_KEY, token).commit();
    }

    public boolean getStatusApp() {
        return sharedPreferences.getBoolean(STATUS_APP_KEY, false);
    }

    /**
     * Trạng thái đăng nhập
     *
     * @param status
     */
    public void putStatusLogin(boolean status) {
        sharedPreferences.edit().putBoolean(STATUS_LOGIN_KEY, status).commit();
    }

    public boolean getStatusLogin() {
        return sharedPreferences.getBoolean(STATUS_LOGIN_KEY, false);
    }

    /**
     * Quyền hiện tại của device
     *
     * @param privilege
     */
    public void putPrivilege(String privilege) {
        sharedPreferences.edit().putString(PRIVILEGE_KEY, privilege).commit();
    }

    public String getPrivilege() {
        return sharedPreferences.getString(PRIVILEGE_KEY, PRIVILEGE_UNKNOWN);
    }

    /**
     * Thời gian gần nhất đồng bộ
     *
     * @param latest
     */
    public void putLatestTransfer(long latest) {
        sharedPreferences.edit().putLong(LATEST_TRANSFER_KEY, latest).commit();
    }

    public long getLatestTransfer() {
        return sharedPreferences.getLong(LATEST_TRANSFER_KEY, 0);
    }

    /**
     * Yêu cầu đồng bộ tài khoản
     *
     * @param value
     */
    public void putRequestSyncAccount(boolean value) {
        sharedPreferences.edit().putBoolean(REQUEST_SYNC_ACCOUNT_KEY, value).commit();
    }

    public boolean getRequestSyncAccount() {
        return sharedPreferences.getBoolean(LATEST_TRANSFER_KEY, false);
    }

    /**
     * @param device_id
     */
    public void putLatestDeviceChild(String device_id) {
        sharedPreferences.edit().putString(LATEST_DEVICE_CHILD, device_id).commit();
    }

    public String getLatestDeviceChild() {
        return sharedPreferences.getString(LATEST_DEVICE_CHILD, null);
    }


    /**
     * Thời gian được tính bằng đơn vị milisecond
     *
     * @param time
     */
    public void putTimeUseInDay(long time) {
        sharedPreferences.edit().putLong(TIME_USE_IN_DAY, time).commit();
    }

    public long getTimeUseInDay() {
        return sharedPreferences.getLong(TIME_USE_IN_DAY, 0);
    }

    /**
     * Mật khẩu client
     */
    public void putPasswordChild(String password) {
        sharedPreferences.edit().putString(PASSWORD_CHILD, password).commit();
    }

    public String getPasswordChild() {
        return sharedPreferences.getString(PASSWORD_CHILD, null);
    }

    /**
     * Thời gian gần nhất đồng bộ
     *
     * @param latest
     */
    public void putLatestDownloadContact(long latest) {
        sharedPreferences.edit().putLong(LATEST_DOWNLOAD_CONTACT_KEY, latest).commit();
    }

    public long getLatestDownloadContact() {
        return sharedPreferences.getLong(LATEST_DOWNLOAD_CONTACT_KEY, 0);
    }


}
