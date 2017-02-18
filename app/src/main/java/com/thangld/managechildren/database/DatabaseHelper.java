package com.thangld.managechildren.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by thangld on 04/02/2017.
 * Thực hiện quản lý cơ sở dữ liệu
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MCDatabase.db";

    private DatabaseHelper mHelper;



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mHelper = this;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Image.SQL_CREATE);
        sqLiteDatabase.execSQL(Video.SQL_CREATE);
        sqLiteDatabase.execSQL(Sms.SQL_CREATE);
        sqLiteDatabase.execSQL(CallLog.SQL_CREATE);
        sqLiteDatabase.execSQL(Phone.SQL_CREATE);
        sqLiteDatabase.execSQL(Email.SQL_CREATE);
        sqLiteDatabase.execSQL(Contact.SQL_CREATE);
        sqLiteDatabase.execSQL(App.SQL_CREATE);
        sqLiteDatabase.execSQL(Version.SQL_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void logQueryAll(String table){
        Log.d("MyChild",mHelper.getReadableDatabase().query(table,null,null,null,null,null,null).toString());
    }

}
