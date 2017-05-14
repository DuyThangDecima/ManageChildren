package com.thangld.managechildren.collector.reader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.thangld.managechildren.storage.controller.DatabaseHelper;
import com.thangld.managechildren.storage.model.ChildModel;

/**
 * Created by thangld on 05/02/2017.
 */

public abstract class AbstractReader extends AsyncTask {

    private String TAG = "AbstractReader";
    protected Context mContext;
    protected SQLiteDatabase mDb;
    protected ContentResolver mContentResolver;
    protected String mChildId;

    public AbstractReader(Context context) {
        this.mContext = context;

        DatabaseHelper helperDb = new DatabaseHelper(mContext);
        this.mDb = helperDb.getWritableDatabase();

        this.mContentResolver = mContext.getContentResolver();
        this.mChildId = ChildModel.QueryHelper.getChildIdActive(mContext);
    }


    protected abstract void increaseVersionDb();

    protected abstract void notifyUpload();

    protected abstract void backup();

    @Override
    protected Object doInBackground(Object[] objects) {
        if(mChildId != null){
            backup();
        }else {
            Log.d("mc_log","mChildId == null");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        // Tăng sion của db
        increaseVersionDb();
        mDb.close();
        notifyUpload();
    }

}
