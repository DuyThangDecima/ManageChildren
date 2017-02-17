package com.thangld.managechildren.collection.reader;

import android.os.AsyncTask;

/**
 * Created by thangld on 05/02/2017.
 */

public class ReaderTask extends AsyncTask {

    protected void copyEntriesDb() {

    }

    // Copy file ảnh, video, audio sanng vùng data của ứng dụng
    protected void copyFiles() {

    }


    @Override
    protected Object doInBackground(Object[] objects) {
        copyEntriesDb();
        copyFiles();
        return null;
    }

}
