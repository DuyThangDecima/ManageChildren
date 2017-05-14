package com.thangld.managechildren.cloud;

import java.util.HashMap;

/**
 * Created by thangld on 29/03/2017.
 */

public class UrlPattern {


    /**
     * URL kết nối
     */
    public static final String HOST = "http://kubernetes-codelab-1378.appspot.com/";
//    public static final String HOST = "http://192.168.1.15:8080/";
    public static final String API_VERSION = "api/v1/";
    public static final String DOWNLOAD = "download";

    public static final String HOST_SECURITY = "https://kubernetes-codelab-1378.appspot.com/";

    /** -----------------------------
     * ----------Cac Uri ----------
     * ----------------------------*/
    public static final String SYNC_ACCOUNT = "sync_account";
    /**
     * Check token
     */
    public static final String CHECK_TOKEN = "check_token";
    /**
     * Get Child
     */
    public static final String GET_LATEST_DEVICE_CHILD = "get_latest_device_child";

    /**
     * Get id need download
     */
    public static final String GET_FILES_NEED_DOWNLOAD = "get_files_need_download";
    /**
     * -------------------------------------
     * -------------  CAC RESOURCE------------
     * -------------------------------------
     */

    /**
     * Child resource
     */
    public static final String CHILD_RESOURCE = "child";
    /**
     * Parent resource
     */
    public static final String PARENT_RESOURCE = "parent";

    /**
     * authentication
     */
    public static final String AUTHENTICATION_RESOURCE = "authentication";


    /**
     * -----------------------------------------------------
     * -------------- CAC PARAM KHI TRANSFER ----------------
     * -----------------------------------------------------
     */

    public static final String PASSWORD_KEY = "password";
    public static final String EMAIL_KEY = "email";
    public static final String IMEI_KEY = "imei";
    public static final String DEVICE_NAME_KEY = "device_name";
    public static final String FULL_NAME_KEY = "full_name";
    public static final String BIRTH_KEY = "birth";
    public static final String ID_SERVER = "id_server";
    public static final String TOKEN_KEY = "token";
    public static final String TOKEN_FCM_KEY = "token_fcm";
    public static final String CHILD_ID_KEY = "child_id";
    public static final String DEVICE_ID_KEY = "device_id";
    public static final String STATUS_LOGIN_KEY = "status_login";
    public static final String DATA_KEY = "data";
    public static final String VERSION_KEY = "version";
    public static final String TYPE_VERSION_KEY = "type_version";

    public static final String INFO_FILE_KEY = "info_file";
    public static final String ID_KEY = "id";
    public static final String _ID_KEY = "_id";
    public static final String FILE_ID_KEY = "file_id";
    public static final String IDS_KEY = "ids";
    public static final String PRIVILEGE_KEY = "privilege";
    public static final String LAT_LOCATION_KEY = "lat_location";
    public static final String LONG_LOCATION_KEY = "long_location";

    public static final int PRIVILEGE_PARENT = 1;
    public static final int PRIVILEGE_CHILD = 0;


    public static final String PARAMS_KEY = "param";

    /**
     * Các key nhận từ server
     */
    public static final String STATUS_KEY = "status";
    public static final String MSG_KEY = "msg";

    public static final String IS_HAVE = "is_have";
    public static final String DEVICE_ID = "device_id";

    public static final int STATUS_ERROR = 0;
    public static final int STATUS_SUCCESS = 1;

    /**
     * Các mã lỗi nhận từ server
     */

    public static final String ERROR_DB_ACTION = "error_1";
    public static final String ERROR_AUTH = "error_2";
    public static final String ERROR_EXIST = "error_3";

    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_KEY = "action";


    public static final HashMap<String, String> STATUS_DB = new HashMap<String, String>() {{
        put("insert_success", "success_11");
        put("get_success", "success_12");
        put("delete_success", "success_13");
        put("update", "success_14");

        put("insert_error", "error_11");
        put("get_error", "error_12");
        put("delete_error", "error_13");
        put("update_error", "error_14");
    }};

}
