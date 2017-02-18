package com.thangld.managechildren.database;

import android.provider.MediaStore;

/**
 * Created by thangld on 05/02/2017.
 */

public class Image {

    public static final String SQL_CREATE =
            "CREATE TABLE " + Image.Contents.TABLE_NAME + " (" +
                    Image.Contents._ID + " INTEGER PRIMARY KEY," +
                    Image.Contents.DATA + " TEXT," +
                    Image.Contents.DISPLAY_NAME + " TEXT," +
                    Image.Contents.SIZE + " INT," +
                    Image.Contents.DATE_TAKEN + " TEXT," +
                    Image.Contents.DESCRIPTION + " TEXT," +
                    Image.Contents.LATITUDE + " TEXT," +
                    Contents.LONGITUDE + " TEXT," +
                    Contents.IS_BACKUP + " INT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + Image.Contents.TABLE_NAME;


    private Image() {
    }

    public static class Contents implements MediaStore.Images.ImageColumns {
        public static final String TABLE_NAME = "image";
        public static final String IS_BACKUP = "is_backup";

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
