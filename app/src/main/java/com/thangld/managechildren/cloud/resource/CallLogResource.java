package com.thangld.managechildren.cloud.resource;

import android.content.Context;

import com.thangld.managechildren.storage.model.CallLogModel;

/**
 * Created by thangld on 15/04/2017.
 */

public class CallLogResource extends JsonResource {

    public CallLogResource(Context context) {
        this.nameResource = "calllog";
        this.table_name = CallLogModel.Contents.TABLE_NAME;
        this.uri = CallLogModel.Contents.CONTENT_URI;
        this.projections = new String[]{
                CallLogModel.Contents.NUMBER,
                CallLogModel.Contents.DURATION,
                CallLogModel.Contents.DATE,
                CallLogModel.Contents.TYPE,
        };
        this.mContext = context;
    }

}
