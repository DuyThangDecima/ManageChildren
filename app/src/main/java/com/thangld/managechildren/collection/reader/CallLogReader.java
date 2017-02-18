package com.thangld.managechildren.collection.reader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.Debug;
import com.thangld.managechildren.database.CallLog;

/**
 * Created by thangld on 09/02/2017.
 */

public class CallLogReader extends ReaderTask {
    public CallLogReader(Context context) {
        super(context);
    }


    protected void backup() {

        Debug.logD("backup" + CallLogReader.class);

        ContentResolver cr = mContext.getContentResolver();
        Cursor cursor = cr.query(Uri.parse("content://call_log/calls"), null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ContentValues contentValues = new ContentValues();
                contentValues.put(CallLog.Contents._ID, cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls._ID)));
                contentValues.put(CallLog.Contents.NUMBER, cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER)));
                contentValues.put(CallLog.Contents.DATE, cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE)));
                contentValues.put(CallLog.Contents.DURATION, cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION)));
                contentValues.put(CallLog.Contents.TYPE, cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE)));
                contentValues.put(CallLog.Contents.IS_BACKUP, Constant.BACKUP_FALSE);
                mDb.insert(CallLog.Contents.TABLE_NAME,null, contentValues);
            } while (cursor.moveToNext());
        }
        cursor.close();
        mDb.close();
    }
}
