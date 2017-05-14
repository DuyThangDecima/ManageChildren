package com.thangld.managechildren.storage.model;

import android.net.Uri;
import android.provider.BaseColumns;

import com.thangld.managechildren.Constant;

/**
 * Created by thangld on 05/02/2017.
 */

public class AudioModel {

    private static final String PATH_AUDIO = "audios";
    /**
     * Câu lệnh tạo bảng
     */
    public static final String SQL_CREATE =
            "CREATE TABLE " + AudioModel.Contents.TABLE_NAME + " (" +
                    AudioModel.Contents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    AudioModel.Contents.DATA + " TEXT," +
                    AudioModel.Contents.DISPLAY_NAME + " TEXT," +
                    AudioModel.Contents.SIZE + " INT," +
                    Contents.DATE_ADDED + " TEXT," +
                    Contents.ID_SERVER + " TEXT," +
                    Contents.ID_CHILD + " TEXT NOT NULL," +
                    AudioModel.Contents.DURATION + " INT," +
                    Contents.IS_BACKUP + " INT NOT NULL)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + AudioModel.Contents.TABLE_NAME;


    private AudioModel() {
    }

    public static class Contents implements BaseColumns {

        /**
         * Các tên cột có trong video
         */
        public static final String TABLE_NAME = "audio";

        /**
         * Duong dan file
         */
        public static final String DATA = "data";
        /**
         * Ten hien thi
         */
        public static final String DISPLAY_NAME = "display_name";
        /**
         * Size file
         */
        public static final String SIZE = "size";
        /**
         * thoi gian add
         */
        public static final String DATE_ADDED = "date_added";

        /**
         * Thoi gian video
         */
        public static final String DURATION = "duration";


        /**
         * Trang thia backup
         */
        public static final String IS_BACKUP = "is_backup";
        /**
         * id cua record tren server
         */
        public static final String ID_SERVER = "id_server";
        /**
         * child_id của record trên server
         */
        public static final String ID_CHILD = "id_child";


        public static final Uri CONTENT_URI = Constant.BASE_CONTENT_URI.buildUpon().appendPath(PATH_AUDIO).build();


    }
}
