package com.thangld.managechildren.storage.model;

import android.net.Uri;
import android.provider.BaseColumns;

import com.thangld.managechildren.Constant;

/**
 * <p>
 *     Duoc dung de luu ten trong danh ba
 * </p>
 * <p>
 *     Lien ket voi 2 bang:
 *     1. Phone
 *     2. Email
 * </p>
 *
 * Created by thangld on 09/02/2017.
 */

public class ContactModel {

    private static final String PATH_CONTACT = "contacts";

    public static final String SQL_CREATE =
            "CREATE TABLE " + ContactModel.Contents.TABLE_NAME + " (" +
                    ContactModel.Contents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Contents.ID_CHILD + " TEXT NOT NULL," +
                    Contents.DISPLAY_NAME + " TEXT NOT NULL," +
                    Contents.IS_BACKUP + " INT NOT NULL)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + ContactModel.Contents.TABLE_NAME;

    private ContactModel() {
    }

    public static class Contents implements BaseColumns{
        public static final Uri CONTENT_URI = Constant.BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTACT).build();

        /**
         * Ten bang
         */
        public static final String TABLE_NAME = "contact";

        /**
         * Name contact
         */
        public static final String DISPLAY_NAME = "display_name";

        /**
         * child_id của record trên server
         */
        public static final String ID_CHILD = "id_child";

        /**
         * Trang thai backup
         */
        public static final String IS_BACKUP = "is_backup";

    }
}
