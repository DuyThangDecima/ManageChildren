package com.thangld.managechildren.collection.observer;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

/**
 * Created by thangld on 12/02/2017.
 */

public class ImageUpdater extends ContentObserver {



    public ImageUpdater(Handler handler) {
        super(handler);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        


    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
    }
}
