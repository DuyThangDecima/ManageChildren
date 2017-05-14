package com.thangld.managechildren.storage.controller;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thangld.managechildren.storage.model.AppModel;
import com.thangld.managechildren.storage.model.AudioModel;
import com.thangld.managechildren.storage.model.CallLogModel;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.ContactModel;
import com.thangld.managechildren.storage.model.EmailModel;
import com.thangld.managechildren.storage.model.ImageModel;
import com.thangld.managechildren.storage.model.LocationModel;
import com.thangld.managechildren.storage.model.PhoneModel;
import com.thangld.managechildren.storage.model.RuleParentModel;
import com.thangld.managechildren.storage.model.SmsModel;
import com.thangld.managechildren.storage.model.VersionModel;
import com.thangld.managechildren.storage.model.VideoModel;

/**
 * Created by thangld on 04/02/2017.
 * Thực hiện quản lý cơ sở dữ liệu
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MCDatabase.db";

    private DatabaseHelper mHelper;
    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mHelper = this;
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 0
        sqLiteDatabase.execSQL(ImageModel.SQL_CREATE);
        // 1
        sqLiteDatabase.execSQL(VideoModel.SQL_CREATE);
        // 2
        sqLiteDatabase.execSQL(CallLogModel.SQL_CREATE);
        // 3
        sqLiteDatabase.execSQL(AppModel.SQL_CREATE);
        //4
        sqLiteDatabase.execSQL(EmailModel.SQL_CREATE);
        //5
        sqLiteDatabase.execSQL(ContactModel.SQL_CREATE);
        //6
        sqLiteDatabase.execSQL(PhoneModel.SQL_CREATE);
        //7
        sqLiteDatabase.execSQL(LocationModel.SQL_CREATE);
        //8
        sqLiteDatabase.execSQL(SmsModel.SQL_CREATE);
        //9
        sqLiteDatabase.execSQL(AudioModel.SQL_CREATE);
        //10
        sqLiteDatabase.execSQL(VersionModel.SQL_CREATE);

        //11
        sqLiteDatabase.execSQL(ChildModel.SQL_CREATE);

        // 12
        sqLiteDatabase.execSQL(RuleParentModel.SQL_CREATE);


    }

    private void initVersion(SQLiteDatabase sqLiteDatabase) {
        try {

            String[] table = new String[]{
                    ImageModel.Contents.TABLE_NAME,
                    VideoModel.Contents.TABLE_NAME,
                    AudioModel.Contents.TABLE_NAME,
                    CallLogModel.Contents.TABLE_NAME,
                    LocationModel.Contents.TABLE_NAME,
                    ContactModel.Contents.TABLE_NAME,
                    SmsModel.Contents.TABLE_NAME,
                    AppModel.Contents.TABLE_NAME
            };

            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            String versionApp = pInfo.versionName;
            String version = versionApp + ".0.0";

            for (int i = 0; i < table.length; i++) {
                sqLiteDatabase.execSQL(
                        "INSERT INTO " + VersionModel.Contents.TABLE_NAME + " (" +
                                VersionModel.Contents.DATA_TABLE + ", " +
                                VersionModel.Contents.NUMBER_VERSION + ") " +
                                "VALUES " +
                                "('" + table[i] + "', " + "'" + version + "')"
                );

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void logQueryAll(String table) {
        Log.d("MyChild", mHelper.getReadableDatabase().query(table, null, null, null, null, null, null).toString());
    }

}
