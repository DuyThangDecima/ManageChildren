package com.thangld.managechildren.database;

import android.provider.MediaStore;

/**
 * Created by thangld on 05/02/2017.
 */

public class Video {

    public static final String SQL_CREATE =
            "CREATE TABLE " + Video.Contents.TABLE_NAME+ " (" +
                    Video.Contents._ID + " INTEGER PRIMARY KEY," +
                    Video.Contents.DATA + " TEXT," +
                    Video.Contents.DISPLAY_NAME + " TEXT," +
                    Video.Contents.SIZE + " INT," +
                    Video.Contents.DATE_TAKEN + " TEXT," +
                    Video.Contents.DESCRIPTION + " TEXT," +
                    Video.Contents.LATITUDE + " TEXT," +
                    Video.Contents.LONGITUDE + " TEXT," +
                    Video.Contents.BOOKMARK + " TEXT," +
                    Video.Contents.DURATION + " INT," +
                    Contents.RESOLUTION + " TEXT," +
                    Contents.IS_BACKUP + " INT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + Video.Contents.TABLE_NAME;



    private Video() {}

    public static class Contents implements MediaStore.Video.VideoColumns{
        public static final String TABLE_NAME = "video";
        public static final String IS_BACKUP = "is_backup";

    }
}
