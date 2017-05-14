package com.thangld.managechildren.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Log;

import com.thangld.managechildren.storage.model.VersionModel;

/**
 * Created by thangld on 25/02/2017.
 * VersionUtils của tất cả các bảng được lưu theo format
 * version_code.version1.version2
 * version_code không có ý nghĩa gì.
 * <p>
 * example:
 * 1.01.5.100
 * <p>
 * Nếu version2 > 65000 thì tăng version1 lên 1
 * </p>
 */

public class VersionUtils {
    /**
     * @param table
     */
    public static String updateVersion(Context context, String table, String idChild) {
        // Lấy vesion cũ
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(VersionModel.Contents.CONTENT_URI,
                null,
                VersionModel.Contents.DATA_TABLE + " = ? AND " + VersionModel.Contents.ID_CHILD + " = ?",
                new String[]{table, idChild},
                null);

        PackageInfo pInfo = null;
        String versionApp = null;
        String version;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionApp = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            // Lấy version cũ
            version = cursor.getString(cursor.getColumnIndex(VersionModel.Contents.NUMBER_VERSION));
            Log.d("mc_log0", version);

            String[] versionInfo = version.split("\\.");

            int version1 = Integer.valueOf(versionInfo[2]);
            int version2 = Integer.valueOf(versionInfo[3]);
            // Tăng version
            if (version2 > 65000) {
                version1++;
                version2 = 0;
            } else {
                version2++;
            }
            version = versionApp + "." + version1 + "." + version2;
            // Cập nhật version
            ContentValues contentValues = new ContentValues();
            contentValues.put(VersionModel.Contents.DATA_TABLE, table);
            contentValues.put(VersionModel.Contents.ID_CHILD, idChild);
            contentValues.put(VersionModel.Contents.NUMBER_VERSION, version);
            resolver.update(VersionModel.Contents.CONTENT_URI, contentValues, VersionModel.Contents.DATA_TABLE + " = ?", new String[]{table});
        } else {
            // Chưa tồn tại trong bảng thì insert vào với version 1.0.0.0
            version = versionApp + ".0.1";
            ContentValues contentValues = new ContentValues();
            contentValues.put(VersionModel.Contents.DATA_TABLE, table);
            contentValues.put(VersionModel.Contents.ID_CHILD, idChild);
            contentValues.put(VersionModel.Contents.NUMBER_VERSION, version);
            resolver.insert(VersionModel.Contents.CONTENT_URI, contentValues);
        }
        return version;
    }


    public static String initVersion(Context context) {
        // Lấy version code hiện tại
        String version;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
            version = version + ".0.0";
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }

    public static int compareVersion(String vA, String vB) {
        String[] va_array = vA.split("\\.");
        String[] vb_array = vB.split("\\.");
        int i;
        for (i = 0; i < 4; i++) {
            if (Integer.valueOf(va_array[i]) > Integer.valueOf(vb_array[i])) {
                return 1;
            } else if (Integer.valueOf(va_array[i]) < Integer.valueOf(vb_array[i])) {
                return -1;
            } else {
                if (i == 3)
                    return 0;
                else
                    continue;
            }
        }
        return 0;
    }

}
