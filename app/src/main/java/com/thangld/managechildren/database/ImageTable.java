package com.thangld.managechildren.database;

import android.provider.MediaStore;

import static com.thangld.managechildren.database.ImageTable.Contents.TABLE_NAME;

/**
 * Created by thangld on 05/02/2017.
 */

public class ImageTable {

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME+ " (" +
                    ImageTable.Contents._ID + " INTEGER PRIMARY KEY," +
                    ImageTable.Contents.DATA + " TEXT," +
                    ImageTable.Contents.DISPLAY_NAME + " TEXT," +
                    ImageTable.Contents.HEIGHT + " TEXT," +
                    ImageTable.Contents.WIDTH + " TEXT," +
                    ImageTable.Contents.MIME_TYPE + " TEXT," +
                    ImageTable.Contents.SIZE + " INT," +
                    ImageTable.Contents.BUCKET_DISPLAY_NAME + " TEXT," +
                    ImageTable.Contents.BUCKET_ID + " TEXT," +
                    ImageTable.Contents.DATE_TAKEN + " TEXT," +
                    ImageTable.Contents.DESCRIPTION + " TEXT," +
                    ImageTable.Contents.LATITUDE + " TEXT," +
                    ImageTable.Contents.LONGITUDE + " TEXT," +
                    ImageTable.Contents.ORIENTATION + " TEXT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + ImageTable.Contents.TABLE_NAME;



    private ImageTable() {}

    public static class Contents implements MediaStore.Images.ImageColumns{
        public static final String TABLE_NAME = "image";
    }

/*    public static class ImageEntry implements BaseColumns {
        public static final String TABLE_NAME = "image";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String BUCKET_DISPLAY_NAME= "bucket_display_name";
        public static final String BUCKET_ID = "bucket_id";
        public static final String DATE_TAKEN = "datetaken";
        public static final String DESCRIPTION = "description";
//        public static final String IS_PRIVATE = "isprivate";
        public static final String isprivate = "subtitle";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
//        public static final String MINI_THUMB_MAGIC = "mini_thumb_magic";
        public static final String ORIENTATION = "orientation";
//        public static final String PICASA_ID = "picasa_id";

    }*/
}
