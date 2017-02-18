package com.thangld.managechildren.collection.reader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;

import com.thangld.managechildren.Debug;
import com.thangld.managechildren.database.BackupFolder;
import com.thangld.managechildren.database.DatabaseHelper;
import com.thangld.managechildren.database.Video;
import com.thangld.managechildren.cloud.Constant;

import java.io.File;

/**
 * Created by thangld on 05/02/2017.
 */

public class VideoReader extends ReaderTask {


    public VideoReader(Context context) {
        super(context);
    }

    @Override
    public void backup() {
        Debug.logD("backup" + VideoReader.class);

        //query
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                Video.Contents._ID,
                Video.Contents.DATA,
                Video.Contents.DISPLAY_NAME,
                Video.Contents.SIZE,
                Video.Contents.DATE_TAKEN,
                Video.Contents.DESCRIPTION,
                Video.Contents.LATITUDE,
                Video.Contents.LONGITUDE,
                Video.Contents.BOOKMARK,
                Video.Contents.DURATION,
                Video.Contents.RESOLUTION
        };
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);

        // tao folder
        File folderVideo = new File(mContext.getFilesDir(), BackupFolder.BACKUPS + File.separator + BackupFolder.VIDEO);
        if (!folderVideo.exists()) {
            folderVideo.mkdirs();
        }

        // copy file & copy entry table
        DatabaseHelper helperDb = new DatabaseHelper(mContext);
        SQLiteDatabase db = helperDb.getWritableDatabase();

        if (cursor.moveToFirst()) {
            do {
                long size = cursor.getLong(cursor.getColumnIndex(Video.Contents.SIZE));

                if( size > Constant.MAX_SIZE_UPLOAD){
                    continue;
                }

                String data = cursor.getString(cursor.getColumnIndex(Video.Contents.DATA));
                String display_name = cursor.getString(cursor.getColumnIndex(Video.Contents.DISPLAY_NAME));


                display_name = copyFiles(data, BackupFolder.VIDEO, display_name);
                data = mContext.getFilesDir().getAbsolutePath()+ File.separator + BackupFolder.BACKUPS + File.separator + BackupFolder.VIDEO + File.separator + display_name;

                ContentValues content = new ContentValues();
                content.put(Video.Contents._ID, cursor.getInt(cursor.getColumnIndex(Video.Contents._ID)));
                content.put(Video.Contents.DATA, data);
                content.put(Video.Contents.DISPLAY_NAME, display_name);
                content.put(Video.Contents.SIZE, cursor.getString(cursor.getColumnIndex(Video.Contents.SIZE)));
                content.put(Video.Contents.DATE_TAKEN, cursor.getString(cursor.getColumnIndex(Video.Contents.DATE_TAKEN)));
                content.put(Video.Contents.DESCRIPTION, cursor.getString(cursor.getColumnIndex(Video.Contents.DESCRIPTION)));
                content.put(Video.Contents.LATITUDE, cursor.getString(cursor.getColumnIndex(Video.Contents.LATITUDE)));
                content.put(Video.Contents.LONGITUDE, cursor.getString(cursor.getColumnIndex(Video.Contents.LONGITUDE)));
                content.put(Video.Contents.BOOKMARK,cursor.getString(cursor.getColumnIndex(Video.Contents.BOOKMARK)));
                content.put(Video.Contents.DURATION,cursor.getString(cursor.getColumnIndex(Video.Contents.DURATION)));
                content.put(Video.Contents.RESOLUTION,cursor.getString(cursor.getColumnIndex(Video.Contents.RESOLUTION)));
                content.put(Video.Contents.IS_BACKUP, com.thangld.managechildren.Constant.BACKUP_FALSE);

                db.insert(Video.Contents.TABLE_NAME, null, content);

            } while (cursor.moveToNext());
        }
        cursor.close();

    }
}
