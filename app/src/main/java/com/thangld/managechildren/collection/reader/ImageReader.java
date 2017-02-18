package com.thangld.managechildren.collection.reader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.thangld.managechildren.Debug;
import com.thangld.managechildren.cloud.Constant;
import com.thangld.managechildren.database.BackupFolder;
import com.thangld.managechildren.database.Image;
import com.thangld.managechildren.database.Video;

import java.io.File;

/**
 * Created by thangld on 05/02/2017.
 */

public class ImageReader extends ReaderTask {

    public ImageReader(Context context) {
        super(context);
    }




    @Override
    protected void backup() {
        Debug.logD("backup" + ImageReader.class);


        //query
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                Image.Contents._ID,
                Image.Contents.DATA,
                Image.Contents.DISPLAY_NAME,
                Image.Contents.SIZE,
                Image.Contents.DATE_TAKEN,
                Image.Contents.DESCRIPTION,
                Image.Contents.LATITUDE,
                Image.Contents.LONGITUDE
        };
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);

        // tao folder
        File folderImage = new File(mContext.getFilesDir(), BackupFolder.BACKUPS + File.separator + BackupFolder.IMAGE);
        if (!folderImage.exists()) {
            folderImage.mkdirs();
        }

        // copy file & copy entry table


        if (cursor.moveToFirst()) {
            do {

                long size = cursor.getLong(cursor.getColumnIndex(Video.Contents.SIZE));

                if( size > Constant.MAX_SIZE_UPLOAD){
                    continue;
                }

                String data = cursor.getString(cursor.getColumnIndex(Image.Contents.DATA));
                String display_name = cursor.getString(cursor.getColumnIndex(Image.Contents.DISPLAY_NAME));
                Log.d("MyChild","size" +cursor.getString(cursor.getColumnIndex(Image.Contents.DATA)));
                Log.d("MyChild","size" +cursor.getString(cursor.getColumnIndex(Image.Contents.SIZE)));


                display_name = copyFiles(data, BackupFolder.IMAGE, display_name);
                data = mContext.getFilesDir().getAbsolutePath() + BackupFolder.BACKUPS + File.separator + BackupFolder.IMAGE + File.separator + display_name;

                ContentValues content = new ContentValues();
                content.put(Image.Contents._ID, cursor.getInt(cursor.getColumnIndex(Image.Contents._ID)));
                content.put(Image.Contents.DATA, data);
                content.put(Image.Contents.DISPLAY_NAME, display_name);
                content.put(Image.Contents.SIZE, cursor.getString(cursor.getColumnIndex(Image.Contents.SIZE)));

                content.put(Image.Contents.DATE_TAKEN, cursor.getString(cursor.getColumnIndex(Image.Contents.DATE_TAKEN)));
                content.put(Image.Contents.DESCRIPTION, cursor.getString(cursor.getColumnIndex(Image.Contents.DESCRIPTION)));
                content.put(Image.Contents.LATITUDE, cursor.getString(cursor.getColumnIndex(Image.Contents.LATITUDE)));
                content.put(Image.Contents.LONGITUDE, cursor.getString(cursor.getColumnIndex(Image.Contents.LONGITUDE)));
                content.put(Image.Contents.IS_BACKUP, com.thangld.managechildren.Constant.BACKUP_FALSE);
                mDb.insert(Image.Contents.TABLE_NAME, null, content);

            } while (cursor.moveToNext());
        }
        cursor.close();
        mDb.close();
    }
}
