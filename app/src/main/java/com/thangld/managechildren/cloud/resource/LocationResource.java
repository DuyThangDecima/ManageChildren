package com.thangld.managechildren.cloud.resource;

import android.content.Context;
import android.location.Location;

import com.thangld.managechildren.cloud.HttpConnection;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.utils.DeviceInfoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by thangld on 15/04/2017.
 */

public class LocationResource extends JsonResource {

    public static final String RESOURCE_NAME = "location";

    public LocationResource(Context context) {
        this.nameResource = RESOURCE_NAME;
        mContext = context;
    }

    public void sendLocation(Location location, String deviceId) throws JSONException {
        JSONObject data = new JSONObject();
        data.put(UrlPattern.LAT_LOCATION_KEY, location.getLatitude());
        data.put(UrlPattern.LONG_LOCATION_KEY, location.getLongitude());
        data.put(UrlPattern.DEVICE_ID_KEY, deviceId);
        post(data);
    }

    public String requestLocation() {
        try {
            String url = UrlPattern.HOST + UrlPattern.API_VERSION + "request_location";
            PreferencesController preferencesController = new PreferencesController(mContext);
            String token = preferencesController.getToken();
            // Nếu không tồn tại token thì không thực hiện gì cả
            if (token == null) {
                // Loi auth
                return "{\"status\":0, \"msg\":\"error_2\"}";
            }
            String imei = DeviceInfoUtils.getImei(mContext);
            String deviceName = DeviceInfoUtils.getDeviceName();
            JSONObject data = new JSONObject();

            data.put(UrlPattern.CHILD_ID_KEY, ChildModel.QueryHelper.getChildIdActive(mContext));
            data.put(UrlPattern.TOKEN_KEY, token);
            data.put(UrlPattern.IMEI_KEY, imei);
            data.put(UrlPattern.DEVICE_NAME_KEY, deviceName);
            return HttpConnection.exePostConnection(new URL(url), data);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
