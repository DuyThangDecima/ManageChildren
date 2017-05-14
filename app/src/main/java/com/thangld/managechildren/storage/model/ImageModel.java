package com.thangld.managechildren.storage.model;

import android.net.Uri;
import android.provider.BaseColumns;

import com.thangld.managechildren.Constant;

/**
 * Created by thangld on 05/02/2017.
 */

public class ImageModel {


    private static final String PATH_IMAGE = "images";

    public static final String SQL_CREATE =
            "CREATE TABLE " + Contents.TABLE_NAME + " (" +
                    Contents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Contents.DATA + " TEXT," +
                    Contents.DISPLAY_NAME + " TEXT," +
                    Contents.SIZE + " TEXT," +
                    Contents.DATE_ADDED + " TEXT," +
                    Contents.ID_SERVER + " TEXT," +
                    Contents.ID_CHILD + " TEXT," +
                    Contents.IS_BACKUP + " INT NOT NULL)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + Contents.TABLE_NAME;


    private ImageModel() {
    }

    public static class Contents implements BaseColumns {
        // Ten ba
        public static final String TABLE_NAME = "image";

        // trạng thái backup
        public static final String IS_BACKUP = "is_backup";

        public static final String DATA = "data";
        public static final String DISPLAY_NAME = "display_name";
        public static final String SIZE = "size";
        public static final String DATE_ADDED = "date_added";
        public static final String ID_SERVER = "id_server";
        /**
         * child_id của record trên server
         */
        public static final String ID_CHILD = "id_child";



        public static final Uri CONTENT_URI = Constant.BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMAGE).build();

    }

}
