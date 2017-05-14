package com.thangld.managechildren.cloud;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thangld.managechildren.cloud.resource.AppResource;
import com.thangld.managechildren.cloud.resource.AudioResource;
import com.thangld.managechildren.cloud.resource.CallLogResource;
import com.thangld.managechildren.cloud.resource.ContactResource;
import com.thangld.managechildren.cloud.resource.ImageResource;
import com.thangld.managechildren.cloud.resource.RuleParentResource;
import com.thangld.managechildren.cloud.resource.SmsResource;
import com.thangld.managechildren.cloud.resource.VideoResource;


public class TransferService extends IntentService {

    private static final String ACTION_UPLOAD = "com.thangld.managechildren.cloud.uploader.action.UPLOAD";
    private static final String ACTION_DOWNLOAD = "com.thangld.managechildren.cloud.uploader.action.DOWNLOAD";
    private static final String ACTION_SYNC_ACCOUNT = "com.thangld.managechildren.cloud.uploader.action.SYNC_ACCOUNT";

    private static final String EXTRA_TYPE = "com.thangld.managechildren.cloud.uploader.extra.TYPE";

    public static final String UPLOAD_SMS = "upload_sms";
    public static final String UPLOAD_APP = "upload_app";
    public static final String UPLOAD_CONTACT = "upload_contact";
    public static final String UPLOAD_CALL_LOG = "upload_call_log";
    public static final String UPLOAD_VIDEO = "upload_video";
    public static final String UPLOAD_IMAGE = "upload_image";
    public static final String UPLOAD_AUDIO = "upload_audio";
    public static final String UPLOAD_RULE_PARENT = "upload_rule_parent";

    public static final String UPLOAD_ALL = "upload_all";


    public static final String DOWNLOAD_SMS = "download_sms";
    public static final String DOWNLOAD_APP = "download_app";
    public static final String DOWNLOAD_CONTACT = "download_contact";
    public static final String DOWNLOAD_CALL_LOG = "download_call_log";
    public static final String DOWNLOAD_VIDEO = "download_video";
    public static final String DOWNLOAD_IMAGE = "download_image";
    public static final String DOWNLOAD_AUDIO = "download_audio";
    public static final String DOWNLOAD_RULE_PARENT = "download_rule_parent";
    public static final String DOWNLOAD_LOCATION = "download_location";
    public static final String DOWNLOAD_ALL = "download_all";


    public TransferService() {
        super("TransferService");
    }

    public static void startActionUpload(Context context, String type) {


        Intent intent = new Intent(context, TransferService.class);
        intent.setAction(ACTION_UPLOAD);
        intent.putExtra(EXTRA_TYPE, type);
        context.startService(intent);
    }

    public static void startActionDownload(Context context, String type) {
        Intent intent = new Intent(context, TransferService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_TYPE, type);
        context.startService(intent);
    }

    public static void startActionSyncAccount(Context context) {
        Intent intent = new Intent(context, TransferService.class);
        intent.setAction(ACTION_SYNC_ACCOUNT);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD.equals(action)) {
                final String type = intent.getStringExtra(EXTRA_TYPE);
                handleActionUpload(type);
            } else if (ACTION_DOWNLOAD.equals(action)) {
                final String type = intent.getStringExtra(EXTRA_TYPE);
                handleActionDownload(type);
            } else if (ACTION_SYNC_ACCOUNT.equals(action)) {
                // TODO implement
                //handleActionSyncAccount();
            }
        }
    }

    private void handleActionUpload(String type) {
        if (UPLOAD_ALL.equals(type)) {
            Log.d("mc_log", "UPLOAD_ALL");
            // 1
            new SmsResource(this).upload();
            // 2
            new CallLogResource(this).upload();
            // 3
            new AppResource(this).upload();
            // 4
            new ImageResource(this).upload();
            // 5
            new ContactResource(this).upload();
            // 6
            new VideoResource(this).upload();
            // 7
            new AudioResource(this).upload();
            // 8
            new RuleParentResource(this).upload();

        } else if (UPLOAD_SMS.equals(type)) {
            // 1
            new SmsResource(this).upload();
        } else if (UPLOAD_CALL_LOG.equals(type)) {
            // 2
            new CallLogResource(this).upload();
        } else if (UPLOAD_APP.equals(type)) {
            Log.d("mc_log", "UPLOAD_APP");
            // 3
            new AppResource(this).upload();
        } else if (UPLOAD_IMAGE.equals(type)) {
            // 4
            new ImageResource(this).upload();
        } else if (UPLOAD_CONTACT.equals(type)) {
            // 5
            new ContactResource(this).upload();
        } else if (UPLOAD_VIDEO.equals(type)) {
            // 6
            new VideoResource(this).upload();
        } else if (UPLOAD_AUDIO.equals(type)) {
            // 7
            new AudioResource(this).upload();
        } else if (UPLOAD_RULE_PARENT.equals(type)) {
            // 8
            new RuleParentResource(this).upload();
        }
    }

    private void handleActionDownload(String type) {
        Log.d("mc_log", "handleActionDownload");
        if (DOWNLOAD_ALL.equals(type)) {
            //1
            new SmsResource(this).download();
            // 2
            new CallLogResource(this).download();
            // 3
            new AppResource(this).download();
            // 4
            new ImageResource(this).download();
            // 5
            new ContactResource(this).download();
            // 6
            new VideoResource(this).download();
            // 7
            new AudioResource(this).download();
            // 8
            new RuleParentResource(this).download();


        } else if (DOWNLOAD_SMS.equals(type)) {
            // 1
            new SmsResource(this).download();
        } else if (DOWNLOAD_CALL_LOG.equals(type)) {
            Log.d("mc_log","DOWNLOAD_CALL_LOG");
            // 2
            new CallLogResource(this).download();

        } else if (DOWNLOAD_APP.equals(type)) {
            // 3
            new AppResource(this).download();
        } else if (DOWNLOAD_IMAGE.equals(type)) {
            // 4
            new ImageResource(this).download();
        } else if (DOWNLOAD_CONTACT.equals(type)) {
            // 5
            new ContactResource(this).download();
        } else if (DOWNLOAD_VIDEO.equals(type)) {
            // 6
            new VideoResource(this).download();
        } else if (DOWNLOAD_AUDIO.equals(type)) {
            // 7
            new AudioResource(this).download();
        } else if (DOWNLOAD_RULE_PARENT.equals(type)) {
            // 8
            new RuleParentResource(this).download();
        }

    }

//    private void handleActionSyncAccount() {
//        // Kiểm tra trạng thái đăng nhập của cha mẹ
//        boolean is_login = AccountResource.checkToken(this);
//        // Nếu đang đăng nhập , lấy danh sách trẻ con
//        if (is_login) {
//            // Lấy danh sách trẻ con hiện tại
//            try {
//                String respond = new ChildResource(this).get(null);
//                JSONObject jsonRespond = new JSONObject(respond);
//                if (jsonRespond.getInt(UrlPattern.STATUS_KEY) == 1) {
//                    // Lay danh sach
//                    JSONArray listChild = jsonRespond.getJSONArray(UrlPattern.DATA_KEY);
//                    // Cập nhật trong bảng child
//                    ContentResolver content = this.getContentResolver();
//                    Cursor cursor;
//                    for (int i = 0; i < listChild.length(); i++) {
//                        JSONObject childObject = listChild.getJSONObject(i);
//                        cursor = content.query(ChildModel.Contents.CONTENT_URI,
//                                null,
//                                ChildModel.Contents._ID + "=?",
//                                new String[]{childObject.getString(ChildModel.Contents._ID)},
//                                null
//                        );
//                        // Nếu có rồi thì không cần
//                        if (cursor.getCount() <= 0) {
//                            ContentValues contentValues = new ContentValues();
//                            contentValues.put(ChildModel.Contents._ID, childObject.getString(ChildModel.Contents._ID));
//                            contentValues.put(ChildModel.Contents.FULL_NAME, childObject.getString(ChildModel.Contents.FULL_NAME));
//                            contentValues.put(ChildModel.Contents.BIRTH, childObject.getString(ChildModel.Contents.BIRTH));
//                            contentValues.put(ChildModel.Contents.ACTIVE, 0); // trạng thái active = 0
//                            content.insert(ChildModel.Contents.CONTENT_URI, contentValues);
//
//                        }
//                    }
//
//                    Intent intent = new Intent();
//                    intent.setAction(PanelActivity.UIBroadcastReceiver.ACTION_UI);
//                    intent.putExtra("is_login", true);
//                    sendBroadcast(intent);
//
//                } else {
//                    String error_id = jsonRespond.getString(UrlPattern.MSG_KEY);
//                    if (UrlPattern.ERROR_AUTH.equals(error_id)) {
//
//                    }
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//        } else {
//            // Gửi broadcast lỗi authen
//            Intent intent = new Intent();
//            intent.setAction(PanelActivity.UIBroadcastReceiver.ACTION_UI);
//            intent.putExtra("is_login", false);
//            sendBroadcast(intent);
//            AccountResource.setLogout(this);
//        }
//
//
//    }
}
