package com.thangld.managechildren;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by thangld on 11/02/2017.
 */

public class Debug {


    public static boolean debug = true;
    public static final String TAG = "MyChild";


    /**
     * log error
     *
     * @param contentLog
     */
    public static void logE(String tag, String contentLog) {
        Log.e(tag, contentLog);

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());

        String content = date + " --- Error:" + tag + ": " + contentLog + "\n";
        write(content);
    }

    /**
     * Log debug
     *
     * @param contentLog
     */
    public static void logD(String tag, String contentLog) {
        Log.d(tag, contentLog);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());

        String content = date + "--- Debug:" + tag + ": " + contentLog + "\n";
        write(content);
    }

    /**
     * Ghi ra file
     *
     * @param content
     */
    private static void write(String content) {
        File root = Environment.getExternalStorageDirectory();

        File file_log = new File(root, ".ManageChild/log.txt");
        try {

            if (!file_log.exists()) {
                new File(root, "ManageChild").mkdir();
                file_log.createNewFile();
            }
            FileOutputStream log_out = new FileOutputStream(file_log.getAbsoluteFile());
            log_out.write(content.getBytes());
            log_out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
