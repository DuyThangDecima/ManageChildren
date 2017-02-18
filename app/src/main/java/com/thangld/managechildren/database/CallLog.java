package com.thangld.managechildren.database;

import android.provider.BaseColumns;

/**
 * Created by thangld on 09/02/2017.
 */

public class CallLog {

    public static final String SQL_CREATE =
            "CREATE TABLE " + CallLog.Contents.TABLE_NAME + " (" +
                    CallLog.Contents._ID + " INTEGER PRIMARY KEY," +
                    CallLog.Contents.NUMBER + " TEXT," +
                    CallLog.Contents.DATE + " TEXT," +
                    Contents.DURATION + " INT," +
                    Contents.TYPE + " INT," +
                    Contents.IS_BACKUP + " INT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + CallLog.Contents.TABLE_NAME;


    private CallLog() {
    }

    public static class Contents implements BaseColumns {
        public static final String TABLE_NAME = "calllog";
        public static final String NUMBER = android.provider.CallLog.Calls.NUMBER;
        public static final String DATE = android.provider.CallLog.Calls.DATE;
        public static final String DURATION = android.provider.CallLog.Calls.DURATION;
        public static final String TYPE = android.provider.CallLog.Calls.TYPE;
        public static final String IS_BACKUP = "is_backup";

    }

}
