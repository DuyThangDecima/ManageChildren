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
import com.thangld.managechildren.storage.model.EmailModel;

import java.util.ArrayList;

/**
 * Created by thangld on 09/02/2017.
 */

public class EmailReader extends AbstractReader {
    private String TAG = "EmailReader";
    public EmailReader(Context context) {
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
        Debug.logD(TAG,"on backup()");
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null, null, null, null);
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                ContentValues contentValues = new ContentValues();
                contentValues.put(EmailModel.Contents.ADDRESS, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                batch.add(ContentProviderOperation.newInsert(EmailModel.Contents.CONTENT_URI).withValues(contentValues).build());
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
        mDb.close();

    }
}


