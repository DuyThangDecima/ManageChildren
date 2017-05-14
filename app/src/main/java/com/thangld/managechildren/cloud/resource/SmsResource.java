package com.thangld.managechildren.cloud.resource;

import android.content.Context;

import com.thangld.managechildren.storage.model.SmsModel;

import java.util.HashMap;

/**
 * Created by thangld on 15/04/2017.
 */

public class SmsResource extends JsonResource {

    public SmsResource(Context context) {
        this.nameResource = "sms";
        this.table_name = SmsModel.Contents.TABLE_NAME;
        this.uri = SmsModel.Contents.CONTENT_URI;
        this.projections = new String[]{
                SmsModel.Contents.ADDRESS,
                SmsModel.Contents.BODY,
                SmsModel.Contents.DATE,
                SmsModel.Contents.TYPE,
        };
        this.mContext = context;
    }

    @Override
    public boolean isRecordExist(HashMap<String, String> hashMap) {
        super.isRecordExist(hashMap);
        String address = hashMap.get(SmsModel.Contents.ADDRESS);
        String body = hashMap.get(SmsModel.Contents.BODY);
        String date = hashMap.get(SmsModel.Contents.DATE);
        String type = hashMap.get(SmsModel.Contents.TYPE);
        return SmsModel.SmsHelper.isExist(mContext,address,body,date,type);
    }
}
