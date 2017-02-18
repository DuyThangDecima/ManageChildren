package com.thangld.managechildren.database;

import android.provider.BaseColumns;

/**
 * Created by thangld on 12/02/2017.
 */

public class Email {
    public static final String SQL_CREATE =
            "CREATE TABLE " + Email.Contents.TABLE_NAME + " (" +
                    Email.Contents._ID + " INT," +
                    Contents.ADDRESS + " TEXT," +
                    Contents.IS_BACKUP + " INT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + Email.Contents.TABLE_NAME;


    private Email() {
    }

    public class Contents implements BaseColumns {
        public static final String TABLE_NAME="mail";
        public static final String ADDRESS = "address";
        public static final String IS_BACKUP = "is_backup";

    }
}
