package com.thangld.managechildren.main.parent;

import android.util.Log;

import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.main.PanelActivity;
import com.thangld.managechildren.storage.controller.DatabaseHelper;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.SmsModel;
import com.thangld.managechildren.utils.NetworkUtils;

/**
 * Created by thangld on 20/04/2017.
 */

public class SmsFragment extends ListNavContentFragment {

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
            Log.d("mc_log", "SmsFragmen getCount()" + mCursor.getCount());

            mCursor.setNotificationUri(getContext().getContentResolver(), SmsModel.Contents.CONTENT_URI);
            cursorAdapter = new CustomCursorAdapter(mContext, mCursor, true);
            cursorAdapter.swapCursor(mCursor);
            listViewContainer.setAdapter(cursorAdapter);
        }
        else{
//            PanelActivity.GetListChild x = new PanelActivity().GetListChild(getActivity());
//            x.execute();
//            setAdapterContent();
        }

    }

    @Override
    protected void checkOnline() {
        // TODO KHông phải khi nào vào cũng check online như thế này

        if (NetworkUtils.isNetworkConnected(mContext)) {
            TransferService.startActionDownload(mContext, TransferService.DOWNLOAD_SMS);
        } else {
            // Hiển thị thanh thông báo không có kết nói
        }


    }

    @Override
    protected void setClickListeners() {
    }
}
