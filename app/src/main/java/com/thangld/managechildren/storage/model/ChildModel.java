package com.thangld.managechildren.storage.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.entry.ChildEntry;

/**
 * Created by thangld on 05/02/2017.
 */

public class ChildModel {

    private static final String PATH_CHILD = "childs";
    /**
     * Câu lệnh tạo bảng
     */
    public static final String SQL_CREATE =
            "CREATE TABLE " + ChildModel.Contents.TABLE_NAME + " (" +
                    ChildModel.Contents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Contents.ID_SERVER + " TEXT," +
                    Contents.FULL_NAME + " TEXT," +
                    Contents.DEVICE_ID + " TEXT," +
                    Contents.BIRTH + " INT," +
                    ChildModel.Contents.ACTIVE + " INT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + ChildModel.Contents.TABLE_NAME;


    private ChildModel() {
    }

    public static class Contents implements BaseColumns {
        /**
         * Các tên cột có trong video columns
         */
        public static final String TABLE_NAME = "child";
        public static final String FULL_NAME = "full_name";
        public static final String ACTIVE = "active";


        public static final String DEVICE_ID = "device_id";


        /**
         * id_server
         */
        public static final String ID_SERVER = "id_server";

        public static final String BIRTH = "birth";

        public static final int ACTIVE_TRUE = 1;
        public static final int ACTIVE_FALSE = 0;
        public static final String NO_LOGIN_BEFORE = "no_login_before";


        public static final Uri CONTENT_URI = Constant.BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHILD).build();

    }

    public static class QueryHelper {

        public static ChildEntry getChildObjectActive(Context context) {

            Cursor cursor =
                    context.getContentResolver().query(
                            ChildModel.Contents.CONTENT_URI,
                            null,
                            ChildModel.Contents.ACTIVE + "=?",
                            new String[]{String.valueOf(1)},
                            null
                    );

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                String full_name = cursor.getString(cursor.getColumnIndex(Contents.FULL_NAME));
                String id_server = cursor.getString(cursor.getColumnIndex(ChildModel.Contents.ID_SERVER));
                int birth = cursor.getInt(cursor.getColumnIndex(Contents.BIRTH));
                return new ChildEntry(full_name, birth, 1, id_server);
            } else {
                return null;
            }
        }

        public static String getChildIdActive(Context context) {
            String child_id = null;
            Cursor cursor =
                    context.getContentResolver().query(
                            ChildModel.Contents.CONTENT_URI,
                            null,
                            ChildModel.Contents.ACTIVE + "=?",
                            new String[]{String.valueOf(1)},
                            null
                    );

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                child_id = cursor.getString(cursor.getColumnIndex(ChildModel.Contents.ID_SERVER));
            }
            return child_id;
        }

        public static void setChildIdActive(Context context, String id_server) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(ChildModel.Contents.ACTIVE, 0);
            context.getContentResolver().update(
                    ChildModel.Contents.CONTENT_URI,
                    contentValues,
                    ChildModel.Contents.ID_SERVER + " != ?",
                    new String[]{id_server});

            contentValues.put(ChildModel.Contents.ACTIVE, 1);
            context.getContentResolver().update(
                    ChildModel.Contents.CONTENT_URI,
                    contentValues,
                    ChildModel.Contents.ID_SERVER + " = ?",
                    new String[]{id_server}
            );
        }

        public static String getDeviceId(Context context, String childId) {
            Cursor cursor = context.getContentResolver().query(
                    Contents.CONTENT_URI,
                    null,
                    Contents.ID_SERVER + " = ?",
                    new String[]{childId},
                    null
            );
            if (cursor == null || cursor.getCount() == 0) {
                return null;
            } else {
                cursor.moveToFirst();
                return cursor.getString(cursor.getColumnIndex(Contents.DEVICE_ID));
            }

        }

        /**
         * Tra ve device_id cua child dang duoc active
         *
         * @param context
         * @return
         */
        public static String getDeviceIdChildActive(Context context) {
            Cursor cursor = context.getContentResolver().query(
                    Contents.CONTENT_URI,
                    null,
                    Contents.ACTIVE + " = ?",
                    new String[]{String.valueOf(Contents.ACTIVE_TRUE)},
                    null
            );
            if (cursor == null || cursor.getCount() == 0) {
                return null;
            } else {
                cursor.moveToFirst();
                return cursor.getString(cursor.getColumnIndex(Contents.DEVICE_ID));
            }

        }


        public static void updateChildDeviceId(Context context, String childId, String deviceId) {
            Log.d("updateChildDeviceId", deviceId);
            ContentValues contentValues = new ContentValues();
            contentValues.put(ChildModel.Contents.DEVICE_ID, deviceId);
            context.getContentResolver().update(
                    ChildModel.Contents.CONTENT_URI,
                    contentValues,
                    ChildModel.Contents.ID_SERVER + " = ?",
                    new String[]{childId}
            );
        }

        public static void insertChild(Context context, String childId, String fullName, int birth, int activeStatus) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contents.ID_SERVER, childId);
            contentValues.put(Contents.FULL_NAME, fullName);
            contentValues.put(Contents.BIRTH, birth);
            contentValues.put(Contents.ACTIVE, activeStatus);
            context.getContentResolver().insert(
                    ChildModel.Contents.CONTENT_URI,
                    contentValues
            );
        }

    }

}
