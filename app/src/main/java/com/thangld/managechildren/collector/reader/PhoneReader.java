package com.thangld.managechildren.collector.reader;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.Debug;
import com.thangld.managechildren.storage.model.PhoneModel;

import java.util.ArrayList;

/**
 * Created by thangld on 09/02/2017.
 */

public class PhoneReader extends AbstractReader {

    private String TAG = "PhoneReader";

    public PhoneReader(Context context) {
        super(context);
    }

    @Override
    protected void increaseVersionDb() {

    }

    @Override
    protected void notifyUpload() {

    }

    @Override
    protected void backup() {

        Debug.logD(TAG, "on backup()");

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                ContentValues contentValues = new ContentValues();
                contentValues.put(PhoneModel.Contents._ID, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)));
                contentValues.put(PhoneModel.Contents.NUMBER, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

                batch.add(ContentProviderOperation.newInsert(PhoneModel.Contents.CONTENT_URI).withValues(contentValues).build());
            } while (cursor.moveToNext());
        }
        try {
            mContentResolver.applyBatch(Constant.AUTHORITY_PROVIDER,batch);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        cursor.close();
    }
}


