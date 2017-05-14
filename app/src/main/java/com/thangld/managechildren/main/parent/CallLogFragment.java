package com.thangld.managechildren.main.parent;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thangld.managechildren.R;
import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.storage.controller.DatabaseHelper;
import com.thangld.managechildren.storage.model.CallLogModel;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.SmsModel;
import com.thangld.managechildren.utils.DateUtils;
import com.thangld.managechildren.utils.NetworkUtils;

/**
 * Created by thangld on 20/04/2017.
 */

public class CallLogFragment extends ListNavContentFragment {

    @Override
    protected void setAdapterContent() {
        String childId = ChildModel.QueryHelper.getChildIdActive(mContext);

        if (childId != null) {
            //        mCursor = mContext.getContentResolver().query(SmsModel.Contents.CONTENT_URI, null, null, null, null);
            DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
            String cmdSql =
                    "SELECT " + SmsModel.Contents.ADDRESS + " AS " + TITLE + ", "
                            + SmsModel.Contents._ID + ", "
                            + "MAX(" + SmsModel.Contents.DATE + "), "
                            + SmsModel.Contents.BODY + " AS " + INFO + " "
                            + "FROM " + SmsModel.Contents.TABLE_NAME + " "
                            + "WHERE " + SmsModel.Contents.ID_CHILD + "= '" + childId + "' "
                            + "GROUP BY " + SmsModel.Contents.ADDRESS + " "
                            + "ORDER BY " + SmsModel.Contents.DATE + " DESC";

            mCursor = databaseHelper.getWritableDatabase().rawQuery(cmdSql, null);

            mCursor = mContext.getContentResolver().query(
                    CallLogModel.Contents.CONTENT_URI,
                    null,
                    SmsModel.Contents.ID_CHILD + " = ? ",
                    new String[]{childId},
                    SmsModel.Contents.DATE + " DESC "
            );

            Log.d("mc_log", "CallLogModel getCount()" + mCursor.getCount());

            mCursor.setNotificationUri(getContext().getContentResolver(), CallLogModel.Contents.CONTENT_URI);
            cursorAdapter = new CallLogAdapter(mContext, mCursor, true);
            cursorAdapter.swapCursor(mCursor);
            listViewContainer.setAdapter(cursorAdapter);
        }
    }

    @Override
    protected void checkOnline() {
        // TODO KHông phải khi nào vào cũng check online như thế này

        if (NetworkUtils.isNetworkConnected(mContext)) {
            TransferService.startActionDownload(mContext, TransferService.DOWNLOAD_CALL_LOG);
        } else {
            // Hiển thị thanh thông báo không có kết nói
        }


    }

    @Override
    protected void setClickListeners() {
    }


    public class CallLogAdapter extends CustomCursorAdapter {

        public CallLogAdapter(Context context, Cursor c, boolean autoRequery) {
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

//            imageView.setImageDrawable(drawable);
            // Extract properties from cursor
            long date = cursor.getLong(cursor.getColumnIndexOrThrow(CallLogModel.Contents.DATE));
            String number  = cursor.getString(cursor.getColumnIndexOrThrow(CallLogModel.Contents.NUMBER));
            String dateCoverted = DateUtils.getDate(date, "dd/MM/yyyy hh:mm:ss");
            // Populate fields with extracted properties
            info.setText(dateCoverted);
            title.setText(String.valueOf(number));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLogModel.Contents.TYPE));
            if (type == CallLog.Calls.INCOMING_TYPE) {
                imageView.setImageResource(R.drawable.phone_comming);
            } else if (type == CallLog.Calls.OUTGOING_TYPE) {
                imageView.setImageResource(R.drawable.phone_outing);
            } else if (type == CallLog.Calls.MISSED_TYPE) {
                imageView.setImageResource(R.drawable.phone_missing);
            }

        }


    }
}
