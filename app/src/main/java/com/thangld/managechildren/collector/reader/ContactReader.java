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
import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.ContactModel;
import com.thangld.managechildren.utils.VersionUtils;

import java.util.ArrayList;

import static android.R.attr.id;

/**
 * Created by thangld on 09/02/2017.
 */

public class ContactReader extends AbstractReader {


    private String TAG = "ContactReader";

    public ContactReader(Context context) {
        super(context);
    }

    @Override
    protected void increaseVersionDb() {
        VersionUtils.updateVersion(mContext, ContactModel.Contents.TABLE_NAME, mChildId);
    }

    @Override
    protected void notifyUpload() {
        TransferService.startActionUpload(mContext, TransferService.UPLOAD_CONTACT);

    }

    @Override
    protected void backup() {

        Debug.logD(TAG, "on backup()");

        ContentResolver cr = mContext.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();
        while (cursor.moveToNext()) {

            Cursor cVersion = cr.query(ContactsContract.RawContacts.CONTENT_URI, null, ContactsContract.RawContacts.CONTACT_ID + " = " + id, null, null);
            cVersion.moveToFirst();

            ContentValues contentValues = new ContentValues();
            contentValues.put(ContactModel.Contents.IS_BACKUP, Constant.BACKUP_FALSE);
            contentValues.put(ContactModel.Contents.ID_CHILD, ChildModel.QueryHelper.getChildIdActive(mContext));
            batch.add(ContentProviderOperation.newInsert(ContactModel.Contents.CONTENT_URI).withValues(contentValues).build());
        }

        try {
            mContentResolver.applyBatch(Constant.AUTHORITY_PROVIDER, batch);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        cursor.close();
    }
}


