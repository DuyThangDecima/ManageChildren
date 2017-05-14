package com.thangld.managechildren.storage.model;

import android.net.Uri;
import android.provider.BaseColumns;

import com.thangld.managechildren.Constant;

/**
 * Created by thangld on 12/02/2017.
 */

public class EmailModel {

    private static final String PATH_EMAIL = "emails";
    public static final String SQL_CREATE =
            "CREATE TABLE " + EmailModel.Contents.TABLE_NAME + " (" +
                    EmailModel.Contents._ID + " INT NOT NULL," +
                    Contents.ADDRESS + " TEXT)";
    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + EmailModel.Contents.TABLE_NAME;


    private EmailModel() {
    }

    public static class Contents implements BaseColumns {

        public static final Uri CONTENT_URI = Constant.BASE_CONTENT_URI.buildUpon().appendPath(PATH_EMAIL).build();
        /**
         * Ten bang
         */
        public static final String TABLE_NAME="email";
        /**
         * Dia chi email
         */
        public static final String ADDRESS = "address";
        /**
         * trang thai backup
         */
        public static final String IS_BACKUP = "is_backup";

    }
}
