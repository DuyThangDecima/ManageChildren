package com.thangld.managechildren.collection.reader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.Debug;
import com.thangld.managechildren.database.Sms;

/**
 * Created by thangld on 09/02/2017.
 */

public class SmsReader extends ReaderTask {
    public SmsReader(Context context) {
        super(context);
    }

    @Override
    protected void backup() {
        Debug.logD("backup" + SmsReader.class);


        ContentResolver cr = mContext.getContentResolver();
        Cursor cursor = cr.query(Uri.parse("content://sms/"), null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Sms.Contents.ADDRESS, cursor.getString(cursor.getColumnIndex(Sms.Contents.ADDRESS)));
                contentValues.put(Sms.Contents.BODY, cursor.getString(cursor.getColumnIndex(Sms.Contents.BODY)));
                contentValues.put(Sms.Contents.DATE, cursor.getInt(cursor.getColumnIndex(Sms.Contents.DATE)));
                contentValues.put(Sms.Contents.STATUS, cursor.getInt(cursor.getColumnIndex(Sms.Contents.STATUS)));
                contentValues.put(Sms.Contents.THREAD_ID, cursor.getInt(cursor.getColumnIndex(Sms.Contents.THREAD_ID)));
                contentValues.put(Sms.Contents.TYPE, cursor.getInt(cursor.getColumnIndex(Sms.Contents.TYPE)));
                contentValues.put(Sms.Contents.IS_BACKUP, Constant.BACKUP_FALSE);
                mDb.insert(Sms.Contents.TABLE_NAME,null, contentValues);
            } while (cursor.moveToNext());
        }
        cursor.close();
        mDb.close();

    }
}
