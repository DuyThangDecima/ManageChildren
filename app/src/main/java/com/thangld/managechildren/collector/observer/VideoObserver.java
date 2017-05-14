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
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.VideoModel;
import com.thangld.managechildren.utils.FileUtils;
import com.thangld.managechildren.utils.VersionUtils;

import java.io.File;

/**
 * <p>
 * Lắng nghe sự kiện thay đổi csdl của Video & thực hiện:
 * 1. Thực hiện copy ngay vào db nếu có ảnh mới
 * 2. Thêm vào bảng Video trong csdl
 * <p>
 * </p>
 * Created by thangld on 12/02/2017.
 */

public class VideoObserver extends ContentObserver {

    private String TAG = "VideoObserver";
    private Context mContext;

    public VideoObserver(Handler handler, Context context) {
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
        Uri uriMedia = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Uri uriApp = VideoModel.Contents.CONTENT_URI;

        String[] projectionMedia = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
        };

        String[] projectionApp = new String[]{
                VideoModel.Contents._ID,
                VideoModel.Contents.DATA,
                VideoModel.Contents.DISPLAY_NAME,
                VideoModel.Contents.SIZE,
                VideoModel.Contents.DATE_ADDED,
        };


        String orderBy = VideoModel.Contents._ID + " DESC";
        Cursor cursorMedia = mContext.getContentResolver().query(uriMedia, projectionMedia, null, null, orderBy);
        Cursor cursorApp = mContext.getContentResolver().query(uriApp, projectionApp, null, null, orderBy);
        // Lấy ảnh có thay đổi gần nhất

        int idMedia = 0;
        int idApp = 0;


        if (cursorMedia.moveToFirst()) {
            idMedia = cursorMedia.getInt(cursorMedia.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID));
        }
        if (cursorApp.moveToFirst()) {
            idApp = cursorApp.getInt(cursorApp.getColumnIndexOrThrow(VideoModel.Contents._ID));
        }

        if (idMedia > idApp) {
            /**
             * Neu id media > id app, nghia la vua co anh duoc chup, hoac duoc them vao:
             * 1. Copy file
             * 2. Update csdl
             */
            Log.d(TAG, "idMedia > idApp");

            int id = cursorMedia.getInt(cursorMedia.getColumnIndex(MediaStore.Video.Media._ID));
            Cursor cursor = mContext.getContentResolver().query(VideoModel.Contents.CONTENT_URI,
                    null,
                    VideoModel.Contents._ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                return;
            } else {
                String data = cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Video.Media.DATA));
                String display_name = cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));

                display_name = FileUtils.copyFiles(mContext, data, BackupFolder.VIDEO, display_name);
                data = mContext.getFilesDir().getAbsolutePath() + BackupFolder.BACKUPS + File.separator + BackupFolder.VIDEO + File.separator + display_name;

                ContentValues content = new ContentValues();
                content.put(VideoModel.Contents._ID, id);
                content.put(VideoModel.Contents.DATA, data);
                content.put(VideoModel.Contents.DISPLAY_NAME, display_name);
                content.put(VideoModel.Contents.SIZE, cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Video.Media.SIZE)));
//
//            content.put(VideoModel.Contents.DATE_ADDED, cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Video.VideoColumns.DATE_ADDED)));
//            content.put(VideoModel.Contents.DURATION, cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Video.VideoColumns.DURATION)));
                content.put(VideoModel.Contents.IS_BACKUP, com.thangld.managechildren.Constant.BACKUP_FALSE);
                content.put(VideoModel.Contents.ID_CHILD, childId);

                mContext.getContentResolver().insert(uriApp, content);
                VersionUtils.updateVersion(mContext, VideoModel.Contents.TABLE_NAME, childId);

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
