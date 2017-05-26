package com.thangld.managechildren.cloud.resource;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.cloud.HttpConnection;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.main.parent.RulesFragment;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.ContactModel;
import com.thangld.managechildren.storage.model.EmailModel;
import com.thangld.managechildren.storage.model.PhoneModel;
import com.thangld.managechildren.storage.model.RuleParentModel;
import com.thangld.managechildren.utils.DeviceInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by thangld on 15/04/2017.
 */

public class RuleParentResource extends JsonResource {

    public RuleParentResource(Context context) {
        this.nameResource = "rule_parent";
        this.table_name = RuleParentModel.Contents.TABLE_NAME;
        this.uri = RuleParentModel.Contents.CONTENT_URI;
        this.uri = RuleParentModel.Contents.CONTENT_URI;
        this.projections = new String[]{
                RuleParentModel.Contents.IS_SET_TIME_LIMIT_APP,
                RuleParentModel.Contents.IS_SET_TIME_LIMIT_APP,
                RuleParentModel.Contents.ID_CHILD,
                RuleParentModel.Contents.TIME_LIMIT_APP,
                RuleParentModel.Contents.IS_BACKUP,
        };
        this.mContext = context;
    }

    @Override
    public void onDownloadFinish(String respond) throws JSONException {
        super.onDownloadFinish(respond);
        mContext.sendBroadcast(new Intent(RulesFragment.UIBroadcast.ACTION_RULE_PARENT));
    }

    @Override
    public void upload() {
        Log.d("mc_log", "upload " + this.nameResource);
        // Lay table can update
        String childId = ChildModel.QueryHelper.getChildIdActive(mContext);

        Cursor cursor = mContext.getContentResolver().query(
                RuleParentModel.Contents.CONTENT_URI,
                projections,
                RuleParentModel.Contents.ID_CHILD + " = ?",
                new String[]{childId},
                null
        );

        if (cursor != null && cursor.getCount() > 0) {
            // Chi upload khi co thay doi thooi
            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex(RuleParentModel.Contents.IS_BACKUP)) == Constant.BACKUP_FALSE) {
                String[] numberCol = cursor.getColumnNames();

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(UrlPattern.PRIVILEGE_KEY, new PreferencesController(mContext).getPrivilege());
                    jsonObject.put(UrlPattern.CHILD_ID_KEY, ChildModel.QueryHelper.getChildIdActive(mContext));
                    for (int i = 0; i < numberCol.length; i++) {
                        if (RuleParentModel.Contents.IS_BACKUP.equals(numberCol[i])) {
                            continue;
                        }
                        jsonObject.put(numberCol[i], cursor.getString(i));
                    }
                    Log.d("mc_log", "upload " + this.nameResource + " " + jsonObject.toString());
                    post(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Thực hiện overide lại, không sử dụng parent
    @Override
    public String download()  {
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

        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(UrlPattern.TOKEN_KEY, token);
        hashMap.put(UrlPattern.IMEI_KEY, imei);
        hashMap.put(UrlPattern.DEVICE_NAME_KEY, deviceName);
        hashMap.put(UrlPattern.CHILD_ID_KEY, childId);

        hashMap.put(UrlPattern.PRIVILEGE_KEY, String.valueOf(preferencesController.getPrivilege()));

        String url = UrlPattern.HOST + UrlPattern.API_VERSION + this.nameResource;

        String respondText = HttpConnection.exeGetConnection(url, hashMap);

        if (respondText == null || respondText.length() == 0) {
        } else {
            JSONObject jsonRespond = null;
            try {
                jsonRespond = new JSONObject(respondText);
                if (jsonRespond.getInt(UrlPattern.STATUS_KEY) == 1) {
                    new PreferencesController(mContext).putLatestDownloadContact(System.currentTimeMillis());
                    // Xóa hết bang email, bang phone, bang number
                    JSONObject data = jsonRespond.getJSONObject(UrlPattern.MSG_KEY);
                    int isSetTimeLimit = data.getInt(RuleParentModel.Contents.IS_SET_TIME_LIMIT_APP);
                    long timeLimitApp = data.getLong(RuleParentModel.Contents.TIME_LIMIT_APP);

                    RuleParentModel.RulesParentHelper.setLimitAppTime(mContext, childId, isSetTimeLimit, timeLimitApp);
                } else {
                    String error_id = jsonRespond.getString(UrlPattern.MSG_KEY);
                    if (error_id.equals(UrlPattern.ERROR_AUTH)) {
                        AccountResource.setLogout(mContext);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            onDownloadFinish(respondText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
