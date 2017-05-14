package com.thangld.managechildren;

import android.net.Uri;
import android.os.Environment;

/**
 * Created by thangld on 07/02/2017.
 */

public class Constant {
    public static String MY_TAG = "MyChild";


    /**
     * Lưu const giá trị lưu vào cơ sở dữ liệu
     * IS_BACKUP = 1 => Đã backup. Và ngược lại
     * IS_BACKUP = 0 => Chưa backup
     *
     */
    public static final int BACKUP_TRUE = 1;
    public static final int BACKUP_FALSE = 0;


    /**
     * @link com.database.provide.AbstractaProvider
     *
     */
    public static final String AUTHORITY_PROVIDER = "com.thangld.managechildren.provider";
    /**
     * Base URI. (content://com.thangld.managechildren)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + Constant.AUTHORITY_PROVIDER);




    /**
     *
     * MAX_V
     */
    public static final int MAX_UPLOAD_SIZE = 20 * 1024 * 1024; // 20 MB


    public static final String STORAGE_DATA = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.managechildren";

}
