package com.thangld.managechildren.storage.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.thangld.managechildren.Constant;

/**
 * Quan ly vesion cua csdl
 * <p>
 * - Nếu mà chưa có tài khoản: khi cài app lên version của các bảng sẽ để là 1
 * - Nếu có tài quản: Query thong tin version từ server
 * </p>
 * <p>
 * - VersionUtils của các bảng sẽ tăng lên bất kì có sự thay đổi nào ở client
 * - Khi thực hiện đồng bộ, sẽ so sánh version giữa client và server
 * </p>
 * Created by thangld on 18/02/2017.
 */

public class VersionModel {

    private static final String PATH_VERSION = "versions";

    /**
     * Cau lenh tao csdl
     */
    public static final String SQL_CREATE =
            "CREATE TABLE " + VersionModel.Contents.TABLE_NAME + " (" +
                    VersionModel.Contents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Contents.DATA_TABLE + " TEXT," +
                    Contents.ID_CHILD + " TEXT," +
                    Contents.NUMBER_VERSION + " TEXT)";
    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + VersionModel.Contents.TABLE_NAME;


    private VersionModel() {
    }

    public static class Contents implements BaseColumns {

        /**
         * uri
         */
        public final static Uri CONTENT_URI = Constant.BASE_CONTENT_URI.buildUpon().appendPath(PATH_VERSION).build();


        /**
         * Teen banrg
         */
        public static final String TABLE_NAME = "version";

        /**
         * Bang trong csdl
         */
        public static final String DATA_TABLE = "data_table";
        /**
         * child_id của record trên server
         */
        public static final String ID_CHILD = "id_child";

        /**
         * version
         */
        public static final String NUMBER_VERSION = "number_version";

    }


    public static class VersionHelper {
        public static String getNumberVersion(Context context,  String childId,String tableName) {
            Cursor cursor = context.getContentResolver().query(
                    VersionModel.Contents.CONTENT_URI,
                    null,
                    VersionModel.Contents.DATA_TABLE + " = ? AND "
                            + VersionModel.Contents.ID_CHILD + " = ?",
                    new String[]{tableName, childId},
                    null
            );
            if (cursor == null || cursor.getCount() == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contents.DATA_TABLE, tableName);
                contentValues.put(Contents.ID_CHILD, childId);

                contentValues.put(Contents.NUMBER_VERSION, "0.0.0.0");
                context.getContentResolver().insert(Contents.CONTENT_URI, contentValues);
                return "0.0.0.0";
            }
            else{
                cursor.moveToFirst();
                return cursor.getString(cursor.getColumnIndex(Contents.NUMBER_VERSION));
            }
        }
    }
}

