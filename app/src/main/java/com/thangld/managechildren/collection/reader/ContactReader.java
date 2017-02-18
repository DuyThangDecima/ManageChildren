package com.thangld.managechildren.collection.reader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.Debug;
import com.thangld.managechildren.database.Contact;

/**
 * Created by thangld on 09/02/2017.
 */

public class ContactReader extends ReaderTask{


    public ContactReader(Context context) {
        super(context);
    }

    @Override
    protected void backup() {

        Debug.logD("backup" + ContactReader.class);

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contact.Contents._ID, cursor.getString(cursor.getColumnIndex(Contact.Contents._ID)));
                contentValues.put(Contact.Contents.DISPLAY_NAME, cursor.getString(cursor.getColumnIndex(Contact.Contents.DISPLAY_NAME)));
                contentValues.put(Contact.Contents.IS_BACKUP, Constant.BACKUP_FALSE);
                mDb.insert(Contact.Contents.TABLE_NAME,null, contentValues);
            } while (cursor.moveToNext());
        }
        cursor.close();
        mDb.close();

    }
}


