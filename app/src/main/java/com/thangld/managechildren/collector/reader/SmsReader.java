package com.thangld.managechildren.collector.reader;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.Debug;
import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.storage.model.SmsModel;
import com.thangld.managechildren.utils.VersionUtils;

import java.util.ArrayList;

/**
 * Created by thangld on 09/02/2017.
 */

public class SmsReader extends AbstractReader {
    public static String TAG = "SmsReader";

    public SmsReader(Context context) {
        super(context);
    }

    @Override
    protected void increaseVersionDb() {
        VersionUtils.updateVersion(mContext, SmsModel.Contents.TABLE_NAME, mChildId);
    }

    @Override
    protected void notifyUpload() {
        TransferService.startActionUpload(mContext, TransferService.UPLOAD_SMS);
    }

    @Override
    protected void backup() {
        Debug.logD(TAG, "on backup()");


        ContentResolver cr = mContext.getContentResolver();
        Cursor cursor = cr.query(Uri.parse("content://sms/"), null, null, null, null);


        ArrayList<ContentProviderOperation> batch = new ArrayList<>();


        if (cursor.moveToFirst()) {
            do {
                ContentValues contentValues = new ContentValues();
                contentValues.put(SmsModel.Contents.ADDRESS, cursor.getString(cursor.getColumnIndex(SmsModel.Contents.ADDRESS)));
                contentValues.put(SmsModel.Contents.BODY, cursor.getString(cursor.getColumnIndex(SmsModel.Contents.BODY)));
                contentValues.put(SmsModel.Contents.DATE, cursor.getString(cursor.getColumnIndex(SmsModel.Contents.DATE)));
                contentValues.put(SmsModel.Contents.TYPE, cursor.getLong(cursor.getColumnIndex(SmsModel.Contents.TYPE)));
                contentValues.put(SmsModel.Contents.IS_BACKUP, Constant.BACKUP_FALSE);
                contentValues.put(SmsModel.Contents.ID_CHILD, mChildId);

                String id = cursor.getString(cursor.getColumnIndex(SmsModel.Contents._ID));

                Uri insertUri = SmsModel.Contents.CONTENT_URI.buildUpon().build();

                batch.add(ContentProviderOperation.newInsert(insertUri).withValues(contentValues).build());
            } while (cursor.moveToNext());

            try {
                mContentResolver.applyBatch(Constant.AUTHORITY_PROVIDER, batch);
            } catch (RemoteException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        cursor.close();

    }
}
