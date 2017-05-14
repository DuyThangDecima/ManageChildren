package com.thangld.managechildren.storage.model;

import android.net.Uri;
import android.provider.BaseColumns;

import com.thangld.managechildren.Constant;

/**
 * Created by thangld on 12/02/2017.
 */

public class PhoneModel {


    private static final String PATH_PHONE = "phones";

    public static final String SQL_CREATE =
            "CREATE TABLE " + PhoneModel.Contents.TABLE_NAME + " (" +
                    PhoneModel.Contents._ID + " INT," +
                    Contents.NUMBER + " TEXT)";


    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + PhoneModel.Contents.TABLE_NAME;


    private PhoneModel() {
    }

    public static class Contents implements BaseColumns {
        /**
         * Content su dung trong provider
         */
        public static final Uri CONTENT_URI = Constant.BASE_CONTENT_URI.buildUpon().appendPath(PATH_PHONE).build();
        /**
         * ten bang
         */
        public static final String TABLE_NAME="phone";
        /**
         * so dien thoai
         */
        public static final String NUMBER = "number";

    }
}
