package com.thangld.managechildren.storage.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.utils.VersionUtils;

/**
 * Created by thangld on 09/02/2017.
 */

public class AppModel {
    private static final String PATH_APP = "apps";

    /**
     * Cau lenh tao
     */
    public static final String SQL_CREATE =
            "CREATE TABLE " + AppModel.Contents.TABLE_NAME + " (" +
                    AppModel.Contents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Contents.PACKAGENAME + " TEXT NOT NULL UNIQUE," +
                    Contents.ID_SERVER + " TEXT," +
                    Contents.APP_NAME + " TEXT," +
                    Contents.ID_CHILD + " TEXT NOT NULL," +
                    Contents.TYPE + " INT," +
                    Contents.CATEGORY + " TEXT," +
                    Contents.REMOVED + " INT," +
                    Contents.IS_BACKUP + " TEXT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + AppModel.Contents.TABLE_NAME;

    private AppModel() {
    }

    public static class Contents implements BaseColumns {
        public static final String TABLE_NAME = "app";

        public static final Uri CONTENT_URI = Constant.BASE_CONTENT_URI.buildUpon().appendPath(PATH_APP).build();

        /**
         * Package name cua app
         */
        public static final String PACKAGENAME = "package_name";
        /**
         * Ten hien thi cua app
         */
        public static final String APP_NAME = "app_name";

        /**
         * Đánh dấu app này đã bị xóa, và chưa được cập nhật lên server
         * Khi cập nhật được lên server, bản ghi này sẽ được xóa
         */
        public static final String REMOVED = "removed";

        /**
         * id của record trên server
         */
        public static final String ID_SERVER = "id_server";

        /**
         * child_id của record trên server
         */
        public static final String ID_CHILD = "id_child";

        /**
         * child_id của record trên server
         */
        public static final String CATEGORY = "category";


        /**
         * child_id của record trên server
         */
        public static final String TYPE = "type";


        /**
         * trang thai backuơ
         */
        public static final String IS_BACKUP = "is_backup";


        /**
         * Lưư trạng thái của app đã bị remove hay chưa
         */
        public static final int REMOVED_TRUE = 1;


        public static final int REMOVED_FALSE = 0;

        public static final String TYPE_LIMIT_TIME_APP = "limit_time_app";
        public static final String TYPE_BAN_APP = "ban_app";
        public static final String TYPE_NORMAL_APP = "normal_app";


    }

    public static class AppHelper {
        public static boolean isTypeApp(Context context, String typeApp, String packageName) {
            Cursor cursor = context.getContentResolver().query(
                    Contents.CONTENT_URI,
                    null,
                    Contents.TYPE + " = ? AND " +
                            Contents.PACKAGENAME + " = ?",
                    new String[]{typeApp, packageName},
                    null
            );
            if (cursor != null && cursor.getCount() > 0) {
                return true;
            } else {
                return false;
            }
        }


        public static String getTypeApp(Context context, String childId, String packageName) {
            Cursor cursor = context.getContentResolver().query(
                    Contents.CONTENT_URI,
                    null,
                    Contents.PACKAGENAME + " = ?",
                    new String[]{packageName},
                    null
            );
            if (cursor != null && cursor.getCount() > 0) {
                Log.d("mc_log", "getTypeApp packageName " + packageName);
                Log.d("mc_log", "getTypeApp childId " + childId);
                Log.d("mc_log", "getTypeApp cursor.getCount() " + cursor.getCount());
                cursor.moveToFirst();
                return cursor.getString(cursor.getColumnIndex(Contents.TYPE));
            } else {
                return null;
            }
        }

        public static void setTypeApp(Context context, String childId, long id, String type) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(Contents.TYPE, type);
            contentValues.put(Contents.IS_BACKUP, Constant.BACKUP_FALSE);
            context.getContentResolver().update(
                    Contents.CONTENT_URI,
                    contentValues,
                    Contents._ID + " = ?",
                    new String[]{String.valueOf(id)}
            );
            VersionUtils.updateVersion(context, Contents.TABLE_NAME, childId);
        }

        public static boolean isPackageExist(Context context, String childId, String packageName) {
            Cursor cursor = context.getContentResolver().query(
                    Contents.CONTENT_URI,
                    null,
                    Contents.ID_CHILD + " = ? AND " +
                            Contents.PACKAGENAME + " = ?",
                    new String[]{childId, packageName},
                    null
            );
            if (cursor != null && cursor.getCount() > 0) {
                return true;
            } else {
                return false;
            }
        }


    }
}
