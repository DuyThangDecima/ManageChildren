package com.thangld.managechildren.storage.model;

import android.net.Uri;
import android.provider.BaseColumns;

import com.thangld.managechildren.Constant;

/**
 * Created by thangld on 09/02/2017.
 */

public class CallLogModel {

    private static final String PATH_CALLLOG = "calllogs";

    public static final String SQL_CREATE =
            "CREATE TABLE " + CallLogModel.Contents.TABLE_NAME + " (" +
                    Contents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Contents.NUMBER + " TEXT," +
                    Contents.DATE + " TEXT," +
                    Contents.ID_SERVER + " TEXT," +
                    Contents.ID_CHILD + " TEXT NOT NULL," +
                    Contents.DURATION + " INT," +
                    Contents.TYPE + " INT," +
                    Contents.IS_BACKUP + " INT NOT NULL)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + CallLogModel.Contents.TABLE_NAME;


    private CallLogModel() {
    }

    public static class Contents implements BaseColumns {
        public static final String TABLE_NAME = "calllog";


        public static final Uri CONTENT_URI = Constant.BASE_CONTENT_URI.buildUpon().appendPath(PATH_CALLLOG).build();
        /**
         * Sdt
         */
        public static final String NUMBER = android.provider.CallLog.Calls.NUMBER;
        /**
         * Date
         */
        public static final String DATE = android.provider.CallLog.Calls.DATE;
        /**
         * Tgian cuoc goi
         */
        public static final String DURATION = android.provider.CallLog.Calls.DURATION;
        /**
         * Loai cuoc goi:
         * 1.Goi den
         * 2.Goi di
         * 3. Goi nho...
         */

        public static final String TYPE = android.provider.CallLog.Calls.TYPE;

        /**
         * id_server
         */
        public static final String ID_SERVER = "id_server";

        /**
         * child_id của record trên server
         */
        public static final String ID_CHILD = "id_child";

        /**
         * trang thai backip
         */
        public static final String IS_BACKUP = "is_backup";

    }

}
