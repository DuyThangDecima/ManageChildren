package com.thangld.managechildren.storage.model;

import android.net.Uri;
import android.provider.BaseColumns;

import com.thangld.managechildren.Constant;

/**
 * Created by thangld on 05/02/2017.
 */

public class VideoModel {

    private static final String PATH_VIDEO = "videos";
    /**
     * Câu lệnh tạo bảng
     */
    public static final String SQL_CREATE =
            "CREATE TABLE " + VideoModel.Contents.TABLE_NAME + " (" +
                    VideoModel.Contents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    VideoModel.Contents.DATA + " TEXT," +
                    VideoModel.Contents.DISPLAY_NAME + " TEXT," +
                    VideoModel.Contents.SIZE + " INT," +
                    Contents.DATE_ADDED + " TEXT," +
                    VideoModel.Contents.DURATION + " INT," +
                    Contents.ID_SERVER + " TEXT," +
                    Contents.ID_CHILD + " TEXT NOT NULL," +
                    Contents.IS_BACKUP + " INT NOT NULL)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + VideoModel.Contents.TABLE_NAME;


    private VideoModel() {
    }

    public static class Contents implements BaseColumns {

        /**
         * Các tên cột có trong video columns
         */
        public static final String TABLE_NAME = "video";
        public static final String IS_BACKUP = "is_backup";
        public static final String ID_SERVER = "id_server";
        public static final String DISPLAY_NAME = "display_name";
        public static final String SIZE = "size";
        public static final String DATE_ADDED = "date_added";
        public static final String DURATION = "duration";
        public static final String ID_CHILD = "id_child";
        public static final String DATA = "data";

        public static final Uri CONTENT_URI = Constant.BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();

    }
}
