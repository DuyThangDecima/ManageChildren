package com.thangld.managechildren;

import android.util.Log;

/**
 * Created by thangld on 11/02/2017.
 */

public class Debug {

    public static boolean debug = true;
    public static final String TAG = "MyChild";


    public static void logE(String contentLog){
        if(debug){
            Log.e(TAG, contentLog);
        }
    }

    public static void logD(String contentLog){
        if(debug){
            Log.d(TAG, contentLog);
        }
    }


}
