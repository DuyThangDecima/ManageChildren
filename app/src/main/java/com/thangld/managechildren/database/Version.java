package com.thangld.managechildren.database;

import android.provider.BaseColumns;

/**
 * Created by thangld on 18/02/2017.
 */

public class Version {
    public static final String SQL_CREATE =
            "CREATE TABLE " + Version.Contents.TABLE_NAME + " (" +
                    Version.Contents._ID + " INTEGER PRIMARY KEY," +
                    Contents.TABLE + " TEXT," +
                    Contents.NUMBER_VERSION + " TEXT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + Version.Contents.TABLE_NAME;


    private Version() {
    }

    public class Contents implements BaseColumns {
        public static final String TABLE_NAME="version";

        public static final String TABLE = "table";
        public static final String NUMBER_VERSION = "number_version";

    }
}

