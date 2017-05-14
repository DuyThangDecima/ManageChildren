package com.thangld.managechildren.collector.observer;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.SmsModel;
import com.thangld.managechildren.utils.VersionUtils;

/**
 * Created by thangld on 22/02/2017.
 */

public class SmsObserver extends ContentObserver {
    String TAG = "SmsObserver";
    private Context mContext;
    public SmsObserver(Handler handler, Context context) {
        super(handler);
        mContext = context;
    }


    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        String childId = ChildModel.QueryHelper.getChildIdActive(mContext);
        if (childId == null) {
            return;
        }
        // Sắp xếp 2 bảng theo thời gian giảm dần
        // query
        Uri uriSystem = Uri.parse("content://sms/");

        Uri uriApp = SmsModel.Contents.CONTENT_URI;

        String[] projection = new String[]{
                SmsModel.Contents._ID,
                SmsModel.Contents.ADDRESS,
                SmsModel.Contents.BODY,
                SmsModel.Contents.DATE,
                SmsModel.Contents.TYPE
        };

        String orderBy =  SmsModel.Contents._ID + " DESC";
        Cursor cursorSystem = mContext.getContentResolver().query(uriSystem, projection, null, null, orderBy);
        Cursor cursorApp = mContext.getContentResolver().query(uriApp, projection, null, null, orderBy);
        // Lấy ảnh có thay đổi gần nhất

        int idSystem = 0;
        int idApp = 0;


        if(cursorSystem.moveToFirst()){
            idSystem = cursorSystem.getInt(cursorSystem.getColumnIndexOrThrow(SmsModel.Contents._ID));
        }
        if(cursorApp.moveToFirst()){
            idApp = cursorApp.getInt(cursorSystem.getColumnIndexOrThrow(SmsModel.Contents._ID));
        }

        if( idSystem > idApp){
            /**
             * Neu id media > id app, nghia la vua co anh duoc chup, hoac duoc them vao:
             * 1. Copy file
             * 2. Update csdl
             */
            Log.d(TAG,"idSystem > idApp");

            ContentValues content = new ContentValues();
//            content.put(SmsModel.Contents._ID, cursorSystem.getInt(cursorSystem.getColumnIndex(SmsModel.Contents._ID)));
            content.put(SmsModel.Contents.ADDRESS, cursorSystem.getString(cursorSystem.getColumnIndex(SmsModel.Contents.ADDRESS)));
            content.put(SmsModel.Contents.BODY, cursorSystem.getString(cursorSystem.getColumnIndex(SmsModel.Contents.BODY)));
            content.put(SmsModel.Contents.DATE, cursorSystem.getString(cursorSystem.getColumnIndex(SmsModel.Contents.DATE)));

            content.put(SmsModel.Contents.ID_CHILD, childId);
            content.put(SmsModel.Contents.TYPE, cursorSystem.getString(cursorSystem.getColumnIndex(SmsModel.Contents.TYPE)));
            content.put(SmsModel.Contents.IS_BACKUP, com.thangld.managechildren.Constant.BACKUP_FALSE);
            mContext.getContentResolver().insert(uriApp,content);

            VersionUtils.updateVersion(mContext, SmsModel.Contents.TABLE_NAME,childId);

        }
        else{
            /**
             * Ảnh được chỉnh sửa như tên...., không quan tâm
             */
            Log.d(TAG,"idSystem <= idApp");

        }

    }


}
