package com.thangld.managechildren.cloud.resource;

import android.content.ContentValues;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.cloud.HttpConnection;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.SmsModel;
import com.thangld.managechildren.utils.DeviceInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by thangld on 19/04/2017.
 */

public class JsonResource extends Resource implements MethodJsonResource {

    @Override
    public void onUploadFinish(String respond) throws JSONException {
        if (nameResource == AppResource.NAME_RESOURCE) {
            // App khong duoc xoa
        } else {
            JSONObject jsonRespond = new JSONObject(respond);
            if (jsonRespond.getInt(UrlPattern.STATUS_KEY) == 1) {
                mContext.getContentResolver().delete(uri, null, null);
            }
        }
    }

    @Override
    public String exeUpload(JSONObject jsonData) throws JSONException {
        String respond = post(jsonData);
        onUploadFinish(respond);
        return null;
    }

    @Override
    public String exeDownload(JSONObject dataUpload) throws MalformedURLException, JSONException {

        PreferencesController preferencesController = new PreferencesController(mContext);
        String token = preferencesController.getToken();

        String childId = ChildModel.QueryHelper.getChildIdActive(mContext);
        String deviceId = ChildModel.QueryHelper.getDeviceIdChildActive(mContext);
        // Nếu không tồn tại token thì không thực hiện gì cả
        if (token == null || childId == null || deviceId == null) {
            AccountResource.setLogout(mContext);
            return "error_2";
        }
        String imei = DeviceInfoUtils.getImei(mContext);
        String deviceName = DeviceInfoUtils.getDeviceName();
        if (dataUpload == null) {
            dataUpload = new JSONObject();
        }
        dataUpload.put(UrlPattern.TOKEN_KEY, token);
        dataUpload.put(UrlPattern.IMEI_KEY, imei);
        dataUpload.put(UrlPattern.DEVICE_NAME_KEY, deviceName);
        dataUpload.put(UrlPattern.CHILD_ID_KEY, childId);
        dataUpload.put(UrlPattern.DEVICE_ID_KEY, deviceId);

        String url = UrlPattern.HOST + UrlPattern.API_VERSION + UrlPattern.DOWNLOAD + "/" + this.nameResource;

        String respondText = HttpConnection.exePostConnection(new URL(url), dataUpload);

        if (respondText == null || respondText.length() == 0) {

        } else {
            JSONObject jsonRespond = new JSONObject(respondText);
            if (jsonRespond.getInt(UrlPattern.STATUS_KEY) == 1) {
                JSONArray data = jsonRespond.getJSONArray(UrlPattern.DATA_KEY);
                JSONObject item;
                String action;
                for (int i = 0; i < data.length(); i++) {
                    item = data.getJSONObject(i);
                    action = item.getString(UrlPattern.ACTION_KEY);
                    item.remove(UrlPattern.ACTION_KEY);
//                    String version = item.getString(UrlPattern.VERSION_KEY);
//                    item.remove(UrlPattern.VERSION_KEY);

                    item.put(SmsModel.Contents.ID_CHILD, childId);
                    item.put(SmsModel.Contents.IS_BACKUP, Constant.BACKUP_TRUE);

                    if (UrlPattern.ACTION_ADD.equals(action)) {
                        ContentValues contentValues = new ContentValues();
                        Iterator<?> keys = item.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            // TODO check item.get(key).toString()
                            contentValues.put(key, item.get(key).toString());
                        }
                        mContext.getContentResolver().insert(uri, contentValues);
                    } else if (UrlPattern.ACTION_UPDATE.equals(action)) {
                        ContentValues contentValues = new ContentValues();
                        Iterator<?> keys = item.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            // TODO check item.get(key).toString()
                            contentValues.put(key, item.get(key).toString());
                        }
                        mContext.getContentResolver().insert(uri, contentValues);
                        mContext.getContentResolver().update(uri, contentValues, "id_server = ?", new String[]{item.getString("id_server")});

                    } else if (UrlPattern.ACTION_DELETE.equals(action)) {
                        mContext.getContentResolver().delete(uri, "id_server = ?", new String[]{item.getString("id_server")});
                    }
                }
            } else {
                String error_id = jsonRespond.getString(UrlPattern.MSG_KEY);
                if (error_id.equals(UrlPattern.ERROR_AUTH)) {
                    AccountResource.setLogout(mContext);
                }
            }
        }
        return "";
        //return get();
    }

    @Override
    public boolean isRecordExist(HashMap<String, String> hashMap) {
        return false;
    }

    @Override
    public String get(int id, HashMap<String, String> map) {
        return null;
    }


    /**
     * NOTE: Chú ý nhớ thêm child_id trong map khi cần
     *
     * @param map
     * @return
     */
    @Override
    public String get(HashMap<String, String> map) {
        PreferencesController preferencesController = new PreferencesController(mContext);
        String token = preferencesController.getToken();
        // Nếu không tồn tại token thì không thực hiện gì cả
        if (token == null) {
            // Loi auth
            return "{\"status\":0, \"msg\":\"error_2\"}";
        }

        String imei = DeviceInfoUtils.getImei(mContext);
        String deviceName = DeviceInfoUtils.getDeviceName();

        if (map == null) {
            map = new HashMap<String, String>();
        }

        map.put(UrlPattern.TOKEN_KEY, token);
        map.put(UrlPattern.IMEI_KEY, imei);
        map.put(UrlPattern.DEVICE_NAME_KEY, deviceName);

        String url = UrlPattern.HOST + UrlPattern.API_VERSION + this.nameResource;


        return HttpConnection.exeGetConnection(url, map);
    }

    /**
     * NOTE: Chú ý nhớ thêm child_id trong map khi cần
     *
     * @param data
     * @return
     */
    @Override
    public String post(JSONObject data) {
        String errorDefault = "{\"status\":0, \"msg\":\"unknown\"}";

        try {
            String url = UrlPattern.HOST + UrlPattern.API_VERSION + this.nameResource;
            PreferencesController preferencesController = new PreferencesController(mContext);
            String token = preferencesController.getToken();
            // Nếu không tồn tại token thì không thực hiện gì cả
            if (token == null) {
                // Loi auth
                return "{\"status\":0, \"msg\":\"error_2\"}";
            }
            String imei = DeviceInfoUtils.getImei(mContext);
            String deviceName = DeviceInfoUtils.getDeviceName();
            if (data == null) {
                data = new JSONObject();
            }
            data.put(UrlPattern.TOKEN_KEY, token);
            data.put(UrlPattern.IMEI_KEY, imei);
            data.put(UrlPattern.DEVICE_NAME_KEY, deviceName);
            return HttpConnection.exePostConnection(new URL(url), data);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return errorDefault;
    }

    @Override
    public String put(JSONObject data) {
        String errorDefault = "{\"status\":0, \"msg\":\"unknown\"}";

        try {
            String url = UrlPattern.HOST + UrlPattern.API_VERSION + this.nameResource;
            String token = new PreferencesController(mContext).getToken();
            // Nếu không tồn tại token thì không thực hiện gì cả
            if (token == null) {
                // Loi auth
                return "{\"status\":0, \"msg\":\"error_2\"}";
            }
            String imei = DeviceInfoUtils.getImei(mContext);
            String deviceName = DeviceInfoUtils.getDeviceName();
            if (data == null) {
                data = new JSONObject();
            }
            data.put(UrlPattern.TOKEN_KEY, token);
            data.put(UrlPattern.IMEI_KEY, imei);
            data.put(UrlPattern.DEVICE_NAME_KEY, deviceName);
            return HttpConnection.exePutConnection(new URL(url), data);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return errorDefault;
    }

}
