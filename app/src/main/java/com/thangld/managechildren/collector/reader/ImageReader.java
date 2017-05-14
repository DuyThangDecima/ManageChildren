package com.thangld.managechildren.collector.reader;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.Debug;
import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.storage.controller.BackupFolder;
import com.thangld.managechildren.storage.model.ImageModel;
import com.thangld.managechildren.utils.FileUtils;
import com.thangld.managechildren.utils.VersionUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by thangld on 05/02/2017.
 */

public class ImageReader extends AbstractReader {

    private String TAG = "ImageReader";


    public ImageReader(Context context) {
        super(context);
    }

    @Override
    protected void increaseVersionDb() {
        VersionUtils.updateVersion(mContext, ImageModel.Contents.TABLE_NAME, mChildId);
    }

    @Override
    protected void notifyUpload() {
        TransferService.startActionUpload(mContext, TransferService.UPLOAD_IMAGE);

    }

    @Override
    protected void backup() {
        Debug.logD(TAG, "on backup()");
        Log.d("mc_log", TAG + "on backup()");

        //query
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_TAKEN,
        };
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);

        // tao folder
        File folderImage = new File(mContext.getFilesDir(), BackupFolder.BACKUPS + File.separator + BackupFolder.IMAGE);
        if (!folderImage.exists()) {
            folderImage.mkdirs();
        }

        // copy file & copy entry table
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {

                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));

                if (size > Constant.MAX_UPLOAD_SIZE) {
                    continue;
                }

                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                String sizeFile = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));

                Cursor cursorApp = mContext.getContentResolver().query(
                        ImageModel.Contents.CONTENT_URI,
                        null,
                        ImageModel.Contents.SIZE + " = ? AND "
                                + ImageModel.Contents.DATE_ADDED + " = ?",
                        new String[]{sizeFile, dateAdded},
                        null
                );
                if (cursorApp != null && cursor.getCount() == 0) {
                    display_name = FileUtils.copyFiles(mContext, data, BackupFolder.IMAGE, display_name);
                    data = mContext.getFilesDir().getAbsolutePath() + File.separator + BackupFolder.BACKUPS + File.separator + BackupFolder.IMAGE + File.separator + display_name;

                    ContentValues content = new ContentValues();
                    content.put(ImageModel.Contents.DATA, data);
                    content.put(ImageModel.Contents.DISPLAY_NAME, display_name);
                    content.put(ImageModel.Contents.SIZE, sizeFile);

                    content.put(ImageModel.Contents.DATE_ADDED, dateAdded);
                    content.put(ImageModel.Contents.IS_BACKUP, com.thangld.managechildren.Constant.BACKUP_FALSE);
                    batch.add(ContentProviderOperation.newInsert(ImageModel.Contents.CONTENT_URI).withValues(content).build());
                }

            } while (cursor.moveToNext());
        }
        try {
//            mContentResolver.applyBatch(ImageModel.AUTHORITY,batch);
            mContentResolver.applyBatch(Constant.AUTHORITY_PROVIDER, batch);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        cursor.close();
        mDb.close();
    }
}
