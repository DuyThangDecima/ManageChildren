package com.thangld.managechildren.collection.reader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.thangld.managechildren.Debug;
import com.thangld.managechildren.database.Phone;

/**
 * Created by thangld on 09/02/2017.
 */

public class PhoneReader extends ReaderTask{


    public PhoneReader(Context context) {
        super(context);
    }

    @Override
    protected void backup() {

        Debug.logD("backup" + PhoneReader.class);

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Phone.Contents._ID, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)));
                contentValues.put(Phone.Contents.NUMBER, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                mDb.insert(Phone.Contents.TABLE_NAME,null, contentValues);
            } while (cursor.moveToNext());
        }
        cursor.close();
        mDb.close();

    }
}


