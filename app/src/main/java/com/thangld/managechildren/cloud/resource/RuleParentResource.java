package com.thangld.managechildren.cloud.resource;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.RuleParentModel;

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
                    if(new PreferencesController(mContext).getPrivilege() == PreferencesController.PRIVILEGE_PARENT){
                        jsonObject.put(UrlPattern.PRIVILEGE_KEY, UrlPattern.PRIVILEGE_PARENT);
                    }else{

                        jsonObject.put(UrlPattern.PRIVILEGE_KEY, UrlPattern.PRIVILEGE_CHILD);
                    }
                    jsonObject.put(UrlPattern.CHILD_ID_KEY,ChildModel.QueryHelper.getChildIdActive(mContext));
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

    @Override
    public String download() {

        HashMap<String, String> hashMap = new HashMap<>();
        String childId = ChildModel.QueryHelper.getChildIdActive(mContext);
        hashMap.put(UrlPattern.CHILD_ID_KEY, childId);
        String respond = get(hashMap);
        try {
            JSONObject jsonObject = new JSONObject(respond);
            if (jsonObject.getInt(UrlPattern.STATUS_KEY) == 1) {
                JSONObject data = jsonObject.getJSONObject(UrlPattern.MSG_KEY);
                int isSetTimeLimit = data.getInt(RuleParentModel.Contents.IS_SET_TIME_LIMIT_APP);
                long timeLimitApp = data.getLong(RuleParentModel.Contents.TIME_LIMIT_APP);
                RuleParentModel.RulesParentHelper.setLimitAppTime(mContext, childId, isSetTimeLimit, timeLimitApp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
