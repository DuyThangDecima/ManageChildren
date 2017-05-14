package com.thangld.managechildren.collector.reader;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.Debug;
import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.storage.model.CallLogModel;
import com.thangld.managechildren.storage.model.SmsModel;
import com.thangld.managechildren.utils.VersionUtils;

import java.util.ArrayList;

/**
 * Created by thangld on 09/02/2017.
 */

public class CallLogReader extends AbstractReader {

    private String TAG = "CallLogReader";

    public CallLogReader(Context context) {
        super(context);
    }

    @Override
    protected void increaseVersionDb() {
        VersionUtils.updateVersion(mContext, CallLogModel.Contents.TABLE_NAME, mChildId);
    }

    @Override
    protected void notifyUpload() {
        TransferService.startActionUpload(mContext, TransferService.UPLOAD_CALL_LOG);
    }

    protected void backup() {
        Debug.logD(TAG, "on backup()");
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();
        ContentResolver cr = mContext.getContentResolver();
        Cursor cursor = cr.query(Uri.parse("content://call_log/calls"), null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ContentValues contentValues = new ContentValues();
                contentValues.put(CallLogModel.Contents.NUMBER, cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER)));
                contentValues.put(CallLogModel.Contents.DATE, cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE)));
                contentValues.put(CallLogModel.Contents.DURATION, cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION)));
                contentValues.put(CallLogModel.Contents.TYPE, cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE)));
                contentValues.put(CallLogModel.Contents.IS_BACKUP, Constant.BACKUP_FALSE);
                contentValues.put(SmsModel.Contents.ID_CHILD, mChildId);
                batch.add(ContentProviderOperation.newInsert(CallLogModel.Contents.CONTENT_URI).withValues(contentValues).build());

            } while (cursor.moveToNext());
        }
        try {
            cr.applyBatch(Constant.AUTHORITY_PROVIDER, batch);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        cursor.close();
        mDb.close();
    }
}
