package com.thangld.managechildren.database;

import android.provider.BaseColumns;

/**
 * Created by thangld on 09/02/2017.
 */

public class Sms {
    public static final String SQL_CREATE =
            "CREATE TABLE " + Sms.Contents.TABLE_NAME + " (" +
                    Sms.Contents._ID + " INTEGER PRIMARY KEY," +
                    Sms.Contents.ADDRESS + " TEXT," +
                    Sms.Contents.BODY + " TEXT," +
                    Sms.Contents.DATE + " INT," +
                    Sms.Contents.STATUS + " INT," +
                    Sms.Contents.THREAD_ID + " INT," +
                    Contents.TYPE + " TEXT," +
                    Contents.IS_BACKUP + " INT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + Sms.Contents.TABLE_NAME;


    private Sms() {
    }

    public class Contents implements BaseColumns{
        public static final String TABLE_NAME="sms";

        public static final String ADDRESS = "address";
        public static final String BODY = "body";
        public static final String DATE = "date";
        public static final String STATUS = "status";
        public static final String THREAD_ID = "thread_id";
        public static final String TYPE = "type";
        public static final String IS_BACKUP = "is_backup";
        ;
    }
}
