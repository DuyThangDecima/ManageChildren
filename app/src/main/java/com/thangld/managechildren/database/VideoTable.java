package com.thangld.managechildren.database;

import android.provider.MediaStore;

import static com.thangld.managechildren.database.ImageTable.Contents.TABLE_NAME;

/**
 * Created by thangld on 05/02/2017.
 */

public class VideoTable {

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME+ " (" +
                    VideoTable.Contents._ID + " INTEGER PRIMARY KEY," +
                    VideoTable.Contents.DATA + " TEXT," +
                    VideoTable.Contents.DISPLAY_NAME + " TEXT," +
                    VideoTable.Contents.HEIGHT + " TEXT," +
                    VideoTable.Contents.WIDTH + " TEXT," +
                    VideoTable.Contents.MIME_TYPE + " TEXT," +
                    VideoTable.Contents.SIZE + " INT," +
                    VideoTable.Contents.BUCKET_DISPLAY_NAME + " TEXT," +
                    VideoTable.Contents.BUCKET_ID + " TEXT," +
                    VideoTable.Contents.DATE_TAKEN + " TEXT," +
                    VideoTable.Contents.DESCRIPTION + " TEXT," +
                    VideoTable.Contents.LATITUDE + " TEXT," +
                    VideoTable.Contents.LONGITUDE + " TEXT," +
                    VideoTable.Contents.BOOKMARK + " TEXT," +
                    VideoTable.Contents.DURATION + " INT," +
                    VideoTable.Contents.BOOKMARK + " TEXT," +
                    VideoTable.Contents.RESOLUTION + " TEXT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + VideoTable.Contents.TABLE_NAME;



    private VideoTable() {}

    public static class Contents implements MediaStore.Video.VideoColumns{
        public static final String TABLE_NAME = "video";
    }
}
