package com.thangld.managechildren.database;

import android.provider.BaseColumns;

/**
 * Created by thangld on 09/02/2017.
 */

public class Contact {

    public static final String SQL_CREATE =
            "CREATE TABLE " + Contact.Contents.TABLE_NAME + " (" +
                    Contact.Contents._ID + " INTEGER PRIMARY KEY," +
                    Contact.Contents.DISPLAY_NAME + " TEXT,"+
                    Contents.IS_BACKUP + " INT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + Contact.Contents.TABLE_NAME;

    private Contact() {
    }

    public static class Contents implements BaseColumns{
        public static final String TABLE_NAME = "contact";
        public static final String DISPLAY_NAME = "display_name";
        public static final String IS_BACKUP = "is_backup";

    }
}
