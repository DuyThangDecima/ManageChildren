package com.thangld.managechildren.cloud.resource;

import android.content.Context;

import com.thangld.managechildren.cloud.UrlPattern;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thangld on 05/05/2017.
 */

public class TokenFcmResource extends JsonResource {

    public TokenFcmResource(Context context){
        mContext = context;
        this.nameResource = "token_fcm";

    }
    public void sendTokenFcm(String refreshToken) throws JSONException {
        JSONObject data = new JSONObject();
        data.put(UrlPattern.TOKEN_FCM_KEY, refreshToken);
        post(data);
    }
}
