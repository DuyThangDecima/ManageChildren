package com.thangld.managechildren.main.parent;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thangld.managechildren.R;

/**
 * Created by thangld on 29/04/2017.
 */

public class CustomCursorAdapter extends CursorAdapter {
    protected static final String TITLE = "title";
    protected static final String INFO = "infor";


    public CustomCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_lv_nav_container, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView info = (TextView) view.findViewById(R.id.info);
        TextView title = (TextView) view.findViewById(R.id.title);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        // Extract properties from cursor
        String infoContent = cursor.getString(cursor.getColumnIndexOrThrow(INFO));
        String titleContent = cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
        // Populate fields with extracted properties
        info.setText(infoContent);
        title.setText(String.valueOf(titleContent));
    }
}