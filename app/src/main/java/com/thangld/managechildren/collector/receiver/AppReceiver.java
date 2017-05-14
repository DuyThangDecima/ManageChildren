package com.thangld.managechildren.collector.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Log;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.storage.model.AppModel;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.VersionModel;
import com.thangld.managechildren.utils.VersionUtils;

public class AppReceiver extends BroadcastReceiver {
    private String TAG = "AppReceiver";

    public AppReceiver() {
    }

    /**
     * Khi có sự cài đặt hay gỡ ứng dụng, phải cập nhật lại cơ sở dữ liệu
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        String childId = ChildModel.QueryHelper.getChildIdActive(context);
        if ( childId ==null){
            return;
        }

        String action = intent.getAction();

        String packageName = intent.getData().getSchemeSpecificPart();
        ContentResolver contentResolver = context.getContentResolver();

        final PackageManager pm = context.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        ContentValues contentValues = new ContentValues();
        contentValues.put(AppModel.Contents.PACKAGENAME, packageName);
        contentValues.put(AppModel.Contents.APP_NAME, applicationName);

        contentValues.put(AppModel.Contents.IS_BACKUP, Constant.BACKUP_FALSE);

        if (action == Intent.ACTION_PACKAGE_ADDED) {
            /**
             * Nếu mà cài đặt thêm ứng dụng:
             * <p>
             *     1. Ứng dụng đã được gỡ nhưng chưa được cập nhật lên server(
             *     đang ở trạng thái removed = 1)
             *     - Cập nhật removed thành 0
             * </p>
             * <p>
             *     2. Ứng dụng đó chưa có trong csdl
             *     - Insert vào csdl
             * </p>
             * - Và thực hiện tăng version db,
             */
            contentValues.put(AppModel.Contents.ID_CHILD,childId);
            contentValues.put(AppModel.Contents.REMOVED, 0);
            Cursor cursor = contentResolver.query(AppModel.Contents.CONTENT_URI,
                    null,
                    AppModel.Contents.PACKAGENAME + " = ?",
                    new String[]{packageName},
                    null);
            if (cursor.getCount() > 0) {
                contentResolver.update(AppModel.Contents.CONTENT_URI, contentValues, AppModel.Contents.PACKAGENAME + " = ?", new String[]{packageName});
            } else {
                contentResolver.insert(AppModel.Contents.CONTENT_URI, contentValues);
            }
            VersionUtils.updateVersion(context, VersionModel.Contents.TABLE_NAME, childId);
        } else if (action == Intent.ACTION_PACKAGE_REMOVED) {
            /**
             * Nếu gỡ cài đặt ứng dụng thì cập nhật cột removed = 1
             */
            Log.d(TAG,"packageName" +packageName);
            contentValues.put(AppModel.Contents.REMOVED, 1);
//            contentResolver.update(AppModel.Contents.CONTENT_URI, contentValues, AppModel.Contents.PACKAGENAME, new String[]{packageName});
//            VersionUtils.updateVersion(context, VersionModel.Contents.TABLE_NAME);
        }
    }
}
