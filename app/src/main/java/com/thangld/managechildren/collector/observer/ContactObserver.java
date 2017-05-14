package com.thangld.managechildren.collector.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.thangld.managechildren.Debug;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.ContactModel;
import com.thangld.managechildren.utils.VersionUtils;

/**
 * <p>
 *     Lắng nghe sự thay dổi của danh bạ
 * </p>
 * Created by thangld on 24/02/2017.
 */

public class ContactObserver extends ContentObserver {
    private String TAG = "ContactObserver";
    private Context mContext;
    public ContactObserver(Handler handler, Context context) {
        super(handler);
        mContext = context;
    }

    /**
     * Thực hiện tăng version lên mỗi khi cơ sở dữ liệu thay đổi
     * @param selfChange
     */
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        String childId = ChildModel.QueryHelper.getChildIdActive(mContext);
        if (childId == null) {
            return;
        }

        VersionUtils.updateVersion(mContext, ContactModel.Contents.TABLE_NAME,childId);
        Debug.logD(TAG,"onChange()");
    }
}
