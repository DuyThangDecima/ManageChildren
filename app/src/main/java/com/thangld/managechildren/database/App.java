package com.thangld.managechildren.database;

import android.provider.BaseColumns;

/**
 * Created by thangld on 09/02/2017.
 */

public class App {
    public static final String SQL_CREATE =
            "CREATE TABLE " + App.Contents.TABLE_NAME + " (" +
                    App.Contents._ID + " INTEGER PRIMARY KEY," +
                    Contents.PACKAGENAME + " TEXT," +
                    Contents.DISPLAYNAME + " TEXT," +
                    Contents.IS_BACKUP + " TEXT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + App.Contents.TABLE_NAME;


    private App() {
    }

    public class Contents implements BaseColumns{
        public static final String TABLE_NAME="app";

        public static final String PACKAGENAME = "package_name";
        public static final String DISPLAYNAME = "display_name";
        public static final String IS_BACKUP = "is_backup";

    }
}
