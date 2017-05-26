package com.thangld.managechildren.collector.reader;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.RemoteException;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.storage.model.AppModel;
import com.thangld.managechildren.utils.VersionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thangld on 18/02/2017.
 */

public class AppReader extends AbstractReader {

    String TAG = "AppReader";

    public AppReader(Context context) {
        super(context);
    }


    @Override
    protected void increaseVersionDb() {
        VersionUtils.updateVersion(mContext, AppModel.Contents.TABLE_NAME, mChildId);
    }

    @Override
    protected void notifyUpload() {
        TransferService.startActionUpload(mContext, TransferService.UPLOAD_APP);
    }

    @Override
    protected void backup() {

        PackageManager pm = mContext.getPackageManager();

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();
        List<PackageInfo> packList = pm.getInstalledPackages(0);
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packInfo.applicationInfo.loadLabel(pm).toString();
                String packagename = packInfo.packageName;
                ContentValues contentValues = new ContentValues();

                contentValues.put(AppModel.Contents.APP_NAME, appName);
                contentValues.put(AppModel.Contents.TYPE, AppModel.Contents.TYPE_NORMAL_APP);
                contentValues.put(AppModel.Contents.IS_BACKUP, Constant.BACKUP_FALSE);
                contentValues.put(AppModel.Contents.ID_CHILD, mChildId);

                if (!AppModel.AppHelper.isPackageExist(mContext, mChildId, packagename)) {
                    contentValues.put(AppModel.Contents.PACKAGENAME, packagename);
                    batch.add(ContentProviderOperation.
                            newInsert(AppModel.Contents.CONTENT_URI).
                            withValues(contentValues).
                            build());

                } else {
                    batch.add(ContentProviderOperation.
                            newUpdate(AppModel.Contents.CONTENT_URI).
                            withSelection(AppModel.Contents.PACKAGENAME + " = ?" , new String[]{packagename}).
                            withValue(AppModel.Contents.TYPE, AppModel.Contents.TYPE_NORMAL_APP).
                            withValue(AppModel.Contents.IS_BACKUP, Constant.BACKUP_FALSE).
                            withValue(AppModel.Contents.ID_CHILD, mChildId).
                            build());
                }
            }
        }
        try {
            mContext.getContentResolver().applyBatch(Constant.AUTHORITY_PROVIDER, batch);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}
