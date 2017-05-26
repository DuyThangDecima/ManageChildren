package com.thangld.managechildren.cloud.resource;

import android.content.Context;
import android.content.Intent;

import com.thangld.managechildren.main.parent.RulesFragment;
import com.thangld.managechildren.storage.model.AppModel;

import org.json.JSONException;

/**
 * Created by thangld on 15/04/2017.
 */

public class AppResource extends JsonResource {


    public static final String NAME_RESOURCE = "app";
    public AppResource(Context context) {
        this.nameResource = NAME_RESOURCE;
        this.table_name = AppModel.Contents.TABLE_NAME;
        this.uri = AppModel.Contents.CONTENT_URI;
        this.projections = new String[]{
                AppModel.Contents.APP_NAME,
                AppModel.Contents.PACKAGENAME,
                AppModel.Contents.TYPE,
        };
        this.mContext = context;
    }

    @Override
    public void onDownloadFinish(String respond) throws JSONException {
        super.onDownloadFinish(respond);
        mContext.sendBroadcast(new Intent(RulesFragment.UIBroadcast.ACTION_RULE_PARENT));
    }


}
