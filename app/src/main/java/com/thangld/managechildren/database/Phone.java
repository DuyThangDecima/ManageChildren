package com.thangld.managechildren.database;

import android.provider.BaseColumns;

/**
 * Created by thangld on 12/02/2017.
 */

public class Phone {
    public static final String SQL_CREATE =
            "CREATE TABLE " + Phone.Contents.TABLE_NAME + " (" +
                    Phone.Contents._ID + " INT," +
                    Contents.NUMBER + " TEXT," +
                    Contents.IS_BACKUP + " INT)";


    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + Phone.Contents.TABLE_NAME;


    private Phone() {
    }

    public class Contents implements BaseColumns {
        public static final String TABLE_NAME="phone";
        public static final String NUMBER = "number";
        public static final String IS_BACKUP = "is_backup";

    }
}
