package com.thangld.managechildren.storage.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.thangld.managechildren.Constant;

/**
 * Created by thangld on 09/02/2017.
 */

public class RuleParentModel {

    private static final String PATH_RULES_PARENT = "rule_patents";


    /**
     * Cau lenh tao
     */

    public static final String SQL_CREATE =
            "CREATE TABLE " + RuleParentModel.Contents.TABLE_NAME + " (" +
                    RuleParentModel.Contents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Contents.ID_CHILD + " TEXT," +
                    Contents.IS_SET_TIME_LIMIT_APP + " INT," +
                    Contents.IS_BACKUP + " INT NOT NULL," +
                    Contents.TIME_LIMIT_APP + " INT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + RuleParentModel.Contents.TABLE_NAME;


    private RuleParentModel() {

    }

    public static class Contents implements BaseColumns {
        public static final String TABLE_NAME = "rule_parent";

        public static final Uri CONTENT_URI = Constant.BASE_CONTENT_URI.buildUpon().appendPath(PATH_RULES_PARENT).build();


        public static final String IS_SET_TIME_LIMIT_APP = "is_set_time_limit";

        // public static final String IS_SET_18_CONTENT = "is_set_18_content";


        /**
         * Thời gian được lưu bằng số phút
         */
        public static final String TIME_LIMIT_APP = "time_limit_app";

        /**
         * child_id của record trên server
         */
        public static final String ID_CHILD = "id_child";

        public static final String IS_BACKUP = "is_backup";
    }

    public static class RulesParentHelper {

        /**
         * Lấy thời gian được setup,nếu chưa đc setup. nó set 2h00(120 phút)
         *
         * @param context
         * @param childId
         * @return
         */
        public static long getTimeLimitTimeApp(Context context, String childId) {
            Cursor cursor = context.getContentResolver().query(
                    Contents.CONTENT_URI,
                    null,
                    Contents.ID_CHILD + " = ?",
                    new String[]{childId},
                    null
            );
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getLong(cursor.getColumnIndex(Contents.TIME_LIMIT_APP));
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contents.ID_CHILD, childId);
                contentValues.put(Contents.IS_SET_TIME_LIMIT_APP, 1);
                contentValues.put(Contents.TIME_LIMIT_APP, 120);
                contentValues.put(Contents.IS_BACKUP, Constant.BACKUP_FALSE);
                context.getContentResolver().insert(
                        Contents.CONTENT_URI,
                        contentValues
                );

                // Trả về -1 nghĩa là không get được
                return 120;
            }

        }


        public static boolean isSetTimeLimitApp(Context context, String childId) {
            Cursor cursor = context.getContentResolver().query(
                    Contents.CONTENT_URI,
                    null,
                    Contents.ID_CHILD + " = ?",
                    new String[]{childId},
                    null
            );
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int value = cursor.getInt(cursor.getColumnIndex(Contents.IS_SET_TIME_LIMIT_APP));
                if (value == 1) {
                    return true;
                } else {
                    return false;
                }
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contents.ID_CHILD, childId);
                contentValues.put(Contents.IS_SET_TIME_LIMIT_APP, 1);
                contentValues.put(Contents.TIME_LIMIT_APP, 120);
                contentValues.put(Contents.IS_BACKUP, Constant.BACKUP_FALSE);
                context.getContentResolver().insert(
                        Contents.CONTENT_URI,
                        contentValues
                );

                // Trả về -1 nghĩa là không get được
                return true;
            }
        }

        public static void setLimitAppTime(Context context, String childId, int isSetLimitTime, long time) {
            Cursor cursor = context.getContentResolver().query(
                    Contents.CONTENT_URI,
                    null,
                    Contents.ID_CHILD + " = ?",
                    new String[]{childId},
                    null
            );
            if (cursor != null && cursor.getCount() > 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contents.IS_SET_TIME_LIMIT_APP, isSetLimitTime);
                contentValues.put(Contents.TIME_LIMIT_APP, time);
                contentValues.put(Contents.IS_BACKUP, Constant.BACKUP_FALSE);

                context.getContentResolver().update(Contents.CONTENT_URI,
                        contentValues,
                        Contents.ID_CHILD + " = ?",
                        new String[]{childId}
                );
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contents.ID_CHILD, childId);
                contentValues.put(Contents.IS_SET_TIME_LIMIT_APP, isSetLimitTime);
                contentValues.put(Contents.TIME_LIMIT_APP, time);
                contentValues.put(Contents.IS_BACKUP, Constant.BACKUP_FALSE);
                context.getContentResolver().insert(
                        Contents.CONTENT_URI,
                        contentValues
                );
            }
            return;
        }

    }

}
