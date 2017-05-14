package com.thangld.managechildren.collector.observer;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.thangld.managechildren.storage.controller.BackupFolder;
import com.thangld.managechildren.storage.model.AudioModel;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.utils.FileUtils;
import com.thangld.managechildren.utils.VersionUtils;

import java.io.File;

/**
 * <p>
 * Lắng nghe sự kiện thay đổi csdl của Audio & thực hiện:
 * 1. Thực hiện copy ngay vào db nếu có ảnh mới
 * 2. Thêm vào bảng Audio trong csdl
 * <p>
 * </p>
 * Created by thangld on 12/02/2017.
 */

public class AudioObserver extends ContentObserver {

    private String TAG = "AudioObserver";
    private Context mContext;

    public AudioObserver(Handler handler, Context context) {
        super(handler);
        mContext = context;
    }


    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.d(TAG, "onChange");

        String childId = ChildModel.QueryHelper.getChildIdActive(mContext);
        if (childId == null) {
            return;
        }

        // Sắp xếp 2 bảng theo thời gian giảm dần
        // query
        Uri uriMedia = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri uriApp = AudioModel.Contents.CONTENT_URI;

        String[] projectionMedia = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DURATION
        };

        String[] projectionApp = new String[]{
                AudioModel.Contents._ID,
                AudioModel.Contents.DATA,
                AudioModel.Contents.DISPLAY_NAME,
                AudioModel.Contents.SIZE,
                AudioModel.Contents.DATE_ADDED,
                AudioModel.Contents.DURATION
        };


        String orderBy = AudioModel.Contents._ID + " DESC";
        Cursor cursorMedia = mContext.getContentResolver().query(uriMedia, projectionMedia, null, null, orderBy);
        Cursor cursorApp = mContext.getContentResolver().query(uriApp, projectionApp, null, null, orderBy);
        // Lấy ảnh có thay đổi gần nhất

        int idMedia = 0;
        int idApp = 0;


        if (cursorMedia.moveToFirst()) {
            idMedia = cursorMedia.getInt(cursorMedia.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
        }
        if (cursorApp.moveToFirst()) {
            idApp = cursorApp.getInt(cursorApp.getColumnIndexOrThrow(AudioModel.Contents._ID));
        }

        if (idMedia > idApp) {
            /**
             * Neu id media > id app, nghia la vua co anh duoc chup, hoac duoc them vao:
             * 1. Copy file
             * 2. Update csdl
             */


            Log.d(TAG, "idMedia > idApp");

            int id = cursorMedia.getInt(cursorMedia.getColumnIndex(MediaStore.Audio.Media._ID));
            Cursor cursor = mContext.getContentResolver().query(AudioModel.Contents.CONTENT_URI,
                    null,
                    AudioModel.Contents._ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                return;
            } else {
                String data = cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Audio.Media.DATA));
                String display_name = cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

                display_name = FileUtils.copyFiles(mContext, data, BackupFolder.AUDIO, display_name);
                data = mContext.getFilesDir().getAbsolutePath() + BackupFolder.BACKUPS + File.separator + BackupFolder.AUDIO + File.separator + display_name;

                ContentValues content = new ContentValues();
                content.put(AudioModel.Contents._ID, cursorMedia.getInt(cursorMedia.getColumnIndex(AudioModel.Contents._ID)));
                content.put(AudioModel.Contents.ID_CHILD, childId);
                content.put(AudioModel.Contents.DATA, data);
                content.put(AudioModel.Contents.DISPLAY_NAME, display_name);
                content.put(AudioModel.Contents.SIZE, cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                content.put(AudioModel.Contents.DATE_ADDED, cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)));
                content.put(AudioModel.Contents.DURATION, cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                content.put(AudioModel.Contents.IS_BACKUP, com.thangld.managechildren.Constant.BACKUP_FALSE);

                mContext.getContentResolver().insert(uriApp, content);
                VersionUtils.updateVersion(mContext, AudioModel.Contents.TABLE_NAME, childId);
            }
        } else {
            /**
             * Ảnh được chỉnh sửa như tên...., không quan tâm
             */
            Log.d(TAG, "idMedia <= idApp");

        }

    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
    }
}
