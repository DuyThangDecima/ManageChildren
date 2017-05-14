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
import com.thangld.managechildren.storage.model.SmsModel;
import com.thangld.managechildren.storage.model.VideoModel;
import com.thangld.managechildren.utils.FileUtils;
import com.thangld.managechildren.utils.VersionUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by thangld on 05/02/2017.
 */

public class VideoReader extends AbstractReader {

    private String TAG = "VideoReader";

    public VideoReader(Context context) {
        super(context);
    }

    @Override
    protected void increaseVersionDb() {
        VersionUtils.updateVersion(mContext, VideoModel.Contents.TABLE_NAME, mChildId);
    }

    @Override
    protected void notifyUpload() {
        TransferService.startActionUpload(mContext, TransferService.UPLOAD_VIDEO);
    }


    @Override
    public void backup() {
        Debug.logD(TAG, "on backup()");

        //query
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.DURATION,
        };
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);

        // tao folder
        File folderVideo = new File(mContext.getFilesDir(), BackupFolder.BACKUPS + File.separator + BackupFolder.VIDEO);
        if (!folderVideo.exists()) {
            folderVideo.mkdirs();
        }

        // thuc hien insert vao csdl
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));

                if (size > Constant.MAX_UPLOAD_SIZE) {
                    Log.d("mc_log", "videos size > Constant.MAX_UPLOAD_SIZE");

                    continue;
                }

                // Kiem tra db minh da co chua

                String sizeFile = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));

                Cursor cursorApp = mContext.getContentResolver().query(VideoModel.Contents.CONTENT_URI,
                        null,
                        VideoModel.Contents.DISPLAY_NAME + " = ? AND " +
                                VideoModel.Contents.SIZE + " = ? AND " +
                                VideoModel.Contents.DURATION + " = ? AND " +
                                VideoModel.Contents.DATE_ADDED + " = ?",
                        new String[]{display_name, sizeFile, duration, dateAdded},
                        null);
                // Chi thuc hien insert khi no khong co
                if (cursorApp != null & cursorApp.getCount() == 0) {
                    display_name = FileUtils.copyFiles(mContext, data, BackupFolder.VIDEO, display_name);
                    data = mContext.getFilesDir().getAbsolutePath() + File.separator + BackupFolder.BACKUPS + File.separator + BackupFolder.VIDEO + File.separator + display_name;

                    ContentValues content = new ContentValues();
                    content.put(VideoModel.Contents.DATA, data);
                    content.put(VideoModel.Contents.DISPLAY_NAME, display_name);
                    content.put(VideoModel.Contents.SIZE, sizeFile);
                    content.put(VideoModel.Contents.DATE_ADDED, dateAdded);
                    content.put(VideoModel.Contents.DURATION, duration);
                    content.put(VideoModel.Contents.IS_BACKUP, com.thangld.managechildren.Constant.BACKUP_FALSE);
                    content.put(SmsModel.Contents.ID_CHILD, mChildId);
                    batch.add(ContentProviderOperation.newInsert(VideoModel.Contents.CONTENT_URI).withValues(content).build());
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
