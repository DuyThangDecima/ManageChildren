package com.thangld.managechildren.collector.observer;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.thangld.managechildren.storage.model.CallLogModel;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.utils.VersionUtils;

/**
 * <p>
 * Lắng nghe sự kiện thay đổi csdl của CallLog & thực hiện:
 * 1. Thực hiện copy ngay vào db nếu có ảnh mới
 * 2. Thêm vào bảng CallLog trong csdl
 * <p>
 * </p>
 * Created by thangld on 12/02/2017.
 */

public class CallLogObserver extends ContentObserver {

    private String TAG = "CallLogObserver";
    private Context mContext;
    public CallLogObserver(Handler handler, Context context) {
        super(handler);
        mContext = context;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.d(TAG, "onChange");

        String childId = ChildModel.QueryHelper.getChildIdActive(mContext);
        if (childId == null) {
            return;
        }

        // Sắp xếp 2 bảng theo thời gian giảm dần
        // query
        Uri uriMedia = Uri.parse("content://call_log/calls");
        Uri uriApp = CallLogModel.Contents.CONTENT_URI;

        String[] projection = new String[]{
                CallLogModel.Contents._ID,
                CallLogModel.Contents.NUMBER,
                CallLogModel.Contents.DATE,
                CallLogModel.Contents.DURATION,
                CallLogModel.Contents.TYPE
        };

        String orderBy =  CallLogModel.Contents._ID + " DESC";
        Cursor cursorMedia = mContext.getContentResolver().query(uriMedia, projection, null, null, orderBy);
        Cursor cursorApp = mContext.getContentResolver().query(uriApp, projection, null, null, orderBy);
        // Lấy ảnh có thay đổi gần nhất

        int idMedia = 0;
        int idApp = 0;


        if(cursorMedia.moveToFirst()){
            idMedia = cursorMedia.getInt(cursorMedia.getColumnIndexOrThrow(CallLogModel.Contents._ID));
        }
        if(cursorApp.moveToFirst()){
            idApp = cursorApp.getInt(cursorMedia.getColumnIndexOrThrow(CallLogModel.Contents._ID));
        }

        if( idMedia > idApp){
            /**
             * Neu id media > id app, nghia la vua co anh duoc chup, hoac duoc them vao:
             * 1. Copy file
             * 2. Update csdl
             */
            Log.d(TAG,"idMedia > idApp");

            ContentValues content = new ContentValues();
            content.put(CallLogModel.Contents._ID, cursorMedia.getInt(cursorMedia.getColumnIndex(CallLogModel.Contents._ID)));

            content.put(CallLogModel.Contents.NUMBER, cursorMedia.getString(cursorMedia.getColumnIndex(CallLogModel.Contents.NUMBER)));
            content.put(CallLogModel.Contents.DATE, cursorMedia.getString(cursorMedia.getColumnIndex(CallLogModel.Contents.DATE)));
            content.put(CallLogModel.Contents.DURATION, cursorMedia.getString(cursorMedia.getColumnIndex(CallLogModel.Contents.DURATION)));
            content.put(CallLogModel.Contents.IS_BACKUP, com.thangld.managechildren.Constant.BACKUP_FALSE);
            content.put(CallLogModel.Contents.ID_CHILD, childId);

            mContext.getContentResolver().insert(uriApp,content);
            VersionUtils.updateVersion(mContext, CallLogModel.Contents.TABLE_NAME, childId);

        }
        else{
            /**
             * Ảnh được chỉnh sửa như tên...., không quan tâm
             */
            Log.d(TAG,"idMedia <= idApp");

        }

    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
    }
}
