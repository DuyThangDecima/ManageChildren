package com.thangld.managechildren.main.parent;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thangld.managechildren.R;
import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.storage.controller.DatabaseHelper;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.ContactModel;
import com.thangld.managechildren.utils.NetworkUtils;


public class ContactFragment extends ListNavContentFragment {

    @Override
    protected void setAdapterContent() {
        String childId = ChildModel.QueryHelper.getChildIdActive(mContext);

        if (childId != null) {
            DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
            String cmdSql =
                    "SELECT " + ContactModel.Contents.DISPLAY_NAME + " AS " + TITLE + ", "
                            + ContactModel.Contents._ID + " "
                            + "FROM " + ContactModel.Contents.TABLE_NAME + " "
                            + "WHERE " + ContactModel.Contents.ID_CHILD + "= '" + childId + "' "
                            + "GROUP BY " + ContactModel.Contents.DISPLAY_NAME + " "
                            + "ORDER BY " + ContactModel.Contents.DISPLAY_NAME + " ASC";

            mCursor = databaseHelper.getWritableDatabase().rawQuery(cmdSql, null);
            Log.d("mc_log", "SmsFragmen getCount()" + mCursor.getCount());

            mCursor.setNotificationUri(getContext().getContentResolver(), ContactModel.Contents.CONTENT_URI);
            cursorAdapter = new ContactAdapter(mContext, mCursor, true);
            cursorAdapter.swapCursor(mCursor);
            listViewContainer.setAdapter(cursorAdapter);
        }
    }

    @Override
    protected void checkOnline() {
        // TODO KHông phải khi nào vào cũng check online như thế này

        Cursor cursor = mContext.getContentResolver().query(ContactModel.Contents.CONTENT_URI,null,null,null,null);
        boolean isEmpty = false;
        if( cursor != null && cursor.getCount() <= 0){
            isEmpty = true;
        }
        PreferencesController preferencesController = new PreferencesController(mContext);
        long lastest = preferencesController.getLatestDownloadContact();
        if (System.currentTimeMillis() - lastest > 7 * 24 * 60 *1000 || isEmpty){
            if (NetworkUtils.isNetworkConnected(mContext)) {
                TransferService.startActionDownload(mContext, TransferService.DOWNLOAD_CONTACT);
            } else {
                // Hiển thị thanh thông báo không có kết nói
            }
        }

    }

    @Override
    protected void setClickListeners() {
    }

    public class ContactAdapter extends CustomCursorAdapter {

        public ContactAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.item_lv_nav_container, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView title = (TextView) view.findViewById(R.id.title);
            ImageView imageView = (ImageView) view.findViewById(R.id.icon);
            // Extract properties from cursor
            String titleContent = cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
            // Populate fields with extracted properties
            title.setText(String.valueOf(titleContent));
        }
    }
}
