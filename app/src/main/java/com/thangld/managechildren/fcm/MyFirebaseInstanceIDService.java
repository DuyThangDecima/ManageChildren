package com.thangld.managechildren.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.thangld.managechildren.cloud.resource.TokenFcmResource;
import com.thangld.managechildren.storage.controller.PreferencesController;

import org.json.JSONException;

public class MyFirebaseInstanceIDService  extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    public MyFirebaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        // Kiểm tra trạng thái login, nếu login rồi thì cập nhật
        if(new PreferencesController(this).getStatusLogin()){
            try {
                new TokenFcmResource(this).sendTokenFcm(refreshedToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
