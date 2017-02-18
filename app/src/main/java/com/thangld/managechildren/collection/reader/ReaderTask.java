package com.thangld.managechildren.collection.reader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.thangld.managechildren.Debug;
import com.thangld.managechildren.database.BackupFolder;
import com.thangld.managechildren.database.DatabaseHelper;
import com.thangld.managechildren.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by thangld on 05/02/2017.
 */

public abstract class ReaderTask extends AsyncTask {

    protected Context mContext;
    protected SQLiteDatabase mDb;

    public ReaderTask(Context context) {
        this.mContext = context;

        DatabaseHelper helperDb = new DatabaseHelper(mContext);
        this.mDb = helperDb.getWritableDatabase();
    }





    protected void backup(){
        Debug.logD("backing up");
    };


    /**
     * @param source   Nguồn copy
     * @param folder   Folder đích
     * @param displayName Tên đích
     * @return Tên đã lưu trên đĩa
     */
    protected String copyFiles(String source, String folder, String displayName) {

        File fileOut = new File(mContext.getFilesDir(), File.separator + BackupFolder.BACKUPS + File.separator + folder + File.separator + displayName);
        if (fileOut.exists()) {
            String[] nameExtention = FileUtils.sperateNameExtention(displayName);
            int i = 1;
            while (fileOut.exists()) {
                if (nameExtention[1].equals("")) {
                    displayName = nameExtention[0] + "-" + i;
                } else {
                    displayName = nameExtention[0] + "-" + i + "." + nameExtention[1];
                }
                fileOut = new File(mContext.getFilesDir(), File.separator + BackupFolder.BACKUPS + File.separator + folder + File.separator + displayName);
                i++;
            }
        }

        try {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(fileOut);
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return displayName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    protected Object doInBackground(Object[] objects) {
        backup();
        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        mDb.close();
    }
}
