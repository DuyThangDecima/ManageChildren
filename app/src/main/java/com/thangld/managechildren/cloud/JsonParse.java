package com.thangld.managechildren.cloud;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thangld on 15/04/2017.
 */

public class JsonParse {
    public static JSONArray fromCursor(Cursor cursor) {
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        String content;
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {

                    content = cursor.getString(i);
                    if (content == null) {
                        content = "null";
                    }
                    try {
                        rowObject.put(cursor.getColumnName(i), content);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;

    }
}
