package com.thangld.managechildren.collection.reader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.Debug;
import com.thangld.managechildren.database.Email;

/**
 * Created by thangld on 09/02/2017.
 */

public class EmailReader extends ReaderTask{


    public EmailReader(Context context) {
        super(context);
    }

    @Override
    protected void backup() {
        Debug.logD("backup" + EmailReader.class);
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Email.Contents._ID, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email._ID)));
                contentValues.put(Email.Contents.ADDRESS, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                contentValues.put(Email.Contents.IS_BACKUP, Constant.BACKUP_FALSE);
                mDb.insert(Email.Contents.TABLE_NAME,null, contentValues);
            } while (cursor.moveToNext());
        }
        cursor.close();
        mDb.close();

    }
}


