package com.thangld.managechildren.storage.model;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.thangld.managechildren.Constant;

/**
 * Created by thangld on 05/02/2017.
 */

public class LocationModel {

    private static final String PATH_LOCATION = "locations";
    /**
     * Câu lệnh tạo bảng
     */
    public static final String SQL_CREATE =
            "CREATE TABLE " + LocationModel.Contents.TABLE_NAME + " (" +
                    LocationModel.Contents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    LocationModel.Contents.ADDRESS + " TEXT," +
                    LocationModel.Contents.DATE + " TEXT," +
                    Contents.ID_CHILD + " TEXT NOT NULL," +
                    LocationModel.Contents.LATITUDE + " TEXT," +
                    LocationModel.Contents.LONGITUDE + " TEXT)";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + LocationModel.Contents.TABLE_NAME;


    private LocationModel() {
    }

    public static class Contents implements BaseColumns {

        /**
         * Các tên cột có trong video columns
         */
        public static final String TABLE_NAME = "location";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String DATE = "date";
        public static final String ADDRESS = "address";
        public static final String ID_SERVER = "id_server";
        /**
         * child_id của record trên server
         */
        public static final String ID_CHILD = "id_child";


        public static final Uri CONTENT_URI = Constant.BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();


    }

    public static class LocationHelper {
        public static void insertLocation(Context context, String child, String latLocation, String longLocation, String address) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contents.LATITUDE, latLocation);
            contentValues.put(Contents.LONGITUDE, longLocation);
            contentValues.put(Contents.ADDRESS, address);
            contentValues.put(Contents.DATE, System.currentTimeMillis());
            contentValues.put(Contents.ID_CHILD, child);

            context.getContentResolver().insert(
                    Contents.CONTENT_URI,
                    contentValues
            );
        }
    }
}
