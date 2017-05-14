package com.thangld.managechildren.collector.reader;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.MediaStore;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.Debug;
import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.storage.controller.BackupFolder;
import com.thangld.managechildren.storage.model.AudioModel;
import com.thangld.managechildren.utils.FileUtils;
import com.thangld.managechildren.utils.VersionUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by thangld on 05/02/2017.
 */

public class AudioReader extends AbstractReader {


    private String TAG = "AudioReader";

    public AudioReader(Context context) {
        super(context);
    }

    @Override
    protected void increaseVersionDb() {
        VersionUtils.updateVersion(mContext, AudioModel.Contents.TABLE_NAME, mChildId);
    }

    @Override
    protected void notifyUpload() {
        TransferService.startActionUpload(mContext, TransferService.UPLOAD_AUDIO);

    }

    @Override
    public void backup() {
        Debug.logD(TAG, "on backup");
        //query
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DURATION,
        };
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);

        // tao folder
        File folderAudio = new File(mContext.getFilesDir(), BackupFolder.BACKUPS + File.separator + BackupFolder.AUDIO);
        if (!folderAudio.exists()) {
            folderAudio.mkdirs();
        }

        // thuc hien insert vao csdl
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                if (size > Constant.MAX_UPLOAD_SIZE) {

                    continue;
                }

                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String sizeFile = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                Cursor cursorApp = mContext.getContentResolver().query(
                        AudioModel.Contents.CONTENT_URI,
                        null,
                        AudioModel.Contents.SIZE + " = ? AND " +
                                AudioModel.Contents.DATE_ADDED + " = ? AND " +
                                AudioModel.Contents.DURATION + " = ? ",
                        new String[]{sizeFile, dateAdded, duration},
                        null
                );
                if (cursorApp != null && cursorApp.getCount() == 0) {
                    display_name = FileUtils.copyFiles(mContext, data, BackupFolder.AUDIO, display_name);
                    data = mContext.getFilesDir().getAbsolutePath() + File.separator + BackupFolder.BACKUPS + File.separator + BackupFolder.AUDIO + File.separator + display_name;
                    ContentValues content = new ContentValues();
                    content.put(AudioModel.Contents.DATA, data);
                    content.put(AudioModel.Contents.DISPLAY_NAME, display_name);
                    content.put(AudioModel.Contents.SIZE, sizeFile);
                    content.put(AudioModel.Contents.DATE_ADDED, dateAdded);
                    content.put(AudioModel.Contents.DURATION, duration);
                    content.put(AudioModel.Contents.IS_BACKUP, Constant.BACKUP_FALSE);
                    content.put(AudioModel.Contents.ID_CHILD, mChildId);
                    batch.add(ContentProviderOperation.newInsert(AudioModel.Contents.CONTENT_URI).withValues(content).build());
                }
            } while (cursor.moveToNext());
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
