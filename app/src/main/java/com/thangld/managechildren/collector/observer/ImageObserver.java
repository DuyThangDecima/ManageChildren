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
import com.thangld.managechildren.storage.model.ContactModel;
import com.thangld.managechildren.storage.model.ImageModel;
import com.thangld.managechildren.storage.model.VideoModel;
import com.thangld.managechildren.utils.FileUtils;
import com.thangld.managechildren.utils.VersionUtils;

import java.io.File;

/**
 * <p>
 * Lắng nghe sự kiện thay đổi csdl của image & thực hiện:
 * 1. Thực hiện copy ngay vào db nếu có ảnh mới
 * 2. Thêm vào bảng image trong csdl
 * <p>
 * </p>
 * Created by thangld on 12/02/2017.
 */

public class ImageObserver extends ContentObserver {

    private String TAG = "ImageObserver";
    private Context mContext;

    public ImageObserver(Handler handler, Context context) {
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
        Uri uriMedia = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uriApp = ImageModel.Contents.CONTENT_URI;

        String[] projectionMedia = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_TAKEN,
        };
        String[] projectionApp = new String[]{
                ImageModel.Contents._ID,
                ImageModel.Contents.DATA,
                ImageModel.Contents.DISPLAY_NAME,
                ImageModel.Contents.SIZE,
                ImageModel.Contents.DATE_ADDED,
        };


        String orderBy = ImageModel.Contents._ID + " DESC";
        Cursor cursorMedia = mContext.getContentResolver().query(uriMedia, projectionMedia, null, null, orderBy);
        Cursor cursorApp = mContext.getContentResolver().query(uriApp, projectionApp, null, null, orderBy);
        // Lấy ảnh có thay đổi gần nhất

        int idMedia = 0;
        int idApp = 0;


        if (cursorMedia.moveToFirst()) {
            idMedia = cursorMedia.getInt(cursorMedia.getColumnIndexOrThrow(ImageModel.Contents._ID));
        }
        if (cursorApp.moveToFirst()) {
            idApp = cursorApp.getInt(cursorApp.getColumnIndexOrThrow(ImageModel.Contents._ID));
        }

        if (idMedia > idApp) {
            /**
             * Neu id media > id app, nghia la vua co anh duoc chup, hoac duoc them vao:
             * 1. Copy file
             * 2. Update csdl
             */

            Log.d(TAG, "idMedia > idApp");
            int id = cursorMedia.getInt(cursorMedia.getColumnIndex(MediaStore.Images.Media._ID));
            Cursor cursor = mContext.getContentResolver().query(ImageModel.Contents.CONTENT_URI,
                    null,
                    AudioModel.Contents._ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                return;
            } else {
                String data = cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Images.Media.DATA));
                String display_name = cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));


                display_name = FileUtils.copyFiles(mContext, data, BackupFolder.IMAGE, display_name);
                data = mContext.getFilesDir().getAbsolutePath() + BackupFolder.BACKUPS + File.separator + BackupFolder.IMAGE + File.separator + display_name;


                ContentValues content = new ContentValues();
                content.put(ImageModel.Contents._ID, id);
                content.put(ImageModel.Contents.DATA, data);
                content.put(ImageModel.Contents.DISPLAY_NAME, display_name);
                content.put(ImageModel.Contents.SIZE, cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Images.Media.SIZE)));

                content.put(ImageModel.Contents.DATE_ADDED, cursorMedia.getString(cursorMedia.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)));
                content.put(ImageModel.Contents.IS_BACKUP, com.thangld.managechildren.Constant.BACKUP_FALSE);
                content.put(ImageModel.Contents.ID_CHILD, childId);
                mContext.getContentResolver().insert(uriApp, content);
                VersionUtils.updateVersion(mContext, ContactModel.Contents.TABLE_NAME, childId);
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
