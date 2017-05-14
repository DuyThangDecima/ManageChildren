package com.thangld.managechildren.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.thangld.managechildren.storage.model.ChildModel;

/**
 * Created by thangld on 27/04/2017.
 */

public class DatabaseUtils {

    public static String getChildIdActive(Context context) {
        Cursor cursor =
                context.getContentResolver().query(
                        ChildModel.Contents.CONTENT_URI,
                        null,
                        ChildModel.Contents.ACTIVE + " = ?",
                        new String[]{String.valueOf(1)},
                        null
                );
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String child_id = cursor.getString(cursor.getColumnIndex(ChildModel.Contents.ID_SERVER));
            Log.d("mc_log", "getChildIdActive " + child_id);
        }
        return null;
    }

    public static String setChildIdAcitve(Context context, String id_server) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ChildModel.Contents.ACTIVE, 0);


        context.getContentResolver().update(
                ChildModel.Contents.CONTENT_URI,
                contentValues,
                ChildModel.Contents.ID_SERVER + " != ?",
                new String[]{id_server});

        contentValues.put(ChildModel.Contents.ACTIVE, 1);
        context.getContentResolver().update(
                        ChildModel.Contents.CONTENT_URI,
                        contentValues,
                        ChildModel.Contents.ID_SERVER + " = ?",
                        new String[]{id_server}
                );
        return null;
    }


}
