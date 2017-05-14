package com.thangld.managechildren.main.parent;

import android.util.Log;

import com.thangld.managechildren.storage.controller.DatabaseHelper;
import com.thangld.managechildren.storage.model.ChildModel;

public class ListChildFragment extends ListNavContentFragment {

    public static final String FRAGMENT_TAG = "ListChildFragment";

    @Override
    protected void setAdapterContent() {

        DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
        String cmdSql =
                "SELECT " + ChildModel.Contents._ID + ", "
                        + ChildModel.Contents.FULL_NAME + " AS " + TITLE + ", "
                        + ChildModel.Contents.BIRTH + " AS " + INFO + " "
                        + "FROM " + ChildModel.Contents.TABLE_NAME;

        Log.d("mc_log", "cmdSql = " + cmdSql);
        mCursor = databaseHelper.getWritableDatabase().rawQuery(cmdSql, null);
        Log.d("mc_log", FRAGMENT_TAG + " getCount() " + mCursor.getCount());
        mCursor.setNotificationUri(getContext().getContentResolver(), ChildModel.Contents.CONTENT_URI);
        cursorAdapter = new CustomCursorAdapter(mContext, mCursor, true);
        listViewContainer.setAdapter(cursorAdapter);
    }

    @Override
    protected void checkOnline() {
    }

    @Override
    protected void setClickListeners() {

    }
}
