package com.thangld.managechildren.storage.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.thangld.managechildren.Constant.BASE_CONTENT_URI;

/**
 * Created by thangld on 09/02/2017.
 */

public class SmsModel {

    /**
     * Path component for "smses"
     */
    private static final String PATH_SMS = "smses";

    /**
     * Tao bang khi tao csdl
     */
    public static final String SQL_CREATE =
            "CREATE TABLE " + SmsModel.Contents.TABLE_NAME + " (" +
                    Contents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Contents.ADDRESS + " TEXT," +
                    Contents.BODY + " TEXT," +
                    Contents.DATE + " TEXT," +
                    Contents.TYPE + " TEXT," +
                    Contents.ID_SERVER + " TEXT," +
                    Contents.ID_CHILD + " TEXT NOT NULL," +
                    Contents.IS_BACKUP + " INT NOT NULL)";

    /**
     * Xoa bang trong truong hop su dung online
     */
    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + SmsModel.Contents.TABLE_NAME;


    private SmsModel() {
    }

    public static class Contents implements BaseColumns {
        public static final String TABLE_NAME = "sms";

        /**
         * Fully qualified URI for "sms" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SMS).build();

        /**
         * So dien thoai gui nhan
         */
        public static final String ADDRESS = "address";

        /**
         * Noi dung tin nhan
         */
        public static final String BODY = "body";

        /**
         * Thoi gian gui nhan tin nhan
         */
        public static final String DATE = "date";

        /**
         * id_server
         */
        public static final String TYPE = "type";

        /**
         * child_id của record trên server
         */
        public static final String ID_CHILD = "id_child";

        /**
         * id_server
         */
        public static final String ID_SERVER = "id_server";

        /**
         * Da duoc backup hay chua
         */
        public static final String IS_BACKUP = "is_backup";
        ;
    }

    public static class SmsHelper {
        public static boolean isExist(Context context, String address, String body, String date, String type) {
            Cursor cursor = context.getContentResolver().query(
                    Contents.CONTENT_URI,
                    null,
                    Contents.ADDRESS + " = ? AND " +
                            Contents.BODY + " = ? AND " +
                            Contents.DATE + " = ? AND " +
                            Contents.TYPE + " = ?",
                    new String[]{address, body, date, type},
                    null
            );
            if (cursor == null || cursor.getCount() == 0) {
                return false;
            }
            return true;
        }
    }
}
