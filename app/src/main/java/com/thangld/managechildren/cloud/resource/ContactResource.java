package com.thangld.managechildren.cloud.resource;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.cloud.HttpConnection;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.ContactModel;
import com.thangld.managechildren.storage.model.EmailModel;
import com.thangld.managechildren.storage.model.PhoneModel;
import com.thangld.managechildren.storage.model.VersionModel;
import com.thangld.managechildren.utils.DeviceInfoUtils;
import com.thangld.managechildren.utils.VersionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by thangld on 15/04/2017.
 */

public class ContactResource extends JsonResource {
    private static final String PHONE_KEY = "phone";
    private static final String EMAIL_KEY = "email";


    public ContactResource(Context context) {
        this.nameResource = "contact";
        this.mContext = context;
        this.table_name = "contact";
        this.uri = ContactModel.Contents.CONTENT_URI;
    }

    @Override
    protected JSONArray getDataUpload() throws JSONException {
        // Thực hiện truy vấn cơ sở dữ liệu hiện tại sắp xếp id tăng dần
        ContentResolver resolver = mContext.getContentResolver();
        Cursor currentCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactModel.Contents._ID + " asc");

        JSONArray data_upload = new JSONArray();
        while (currentCursor.moveToNext()) {
            String idContact = currentCursor.getString(currentCursor.getColumnIndex(ContactModel.Contents._ID));
            // Thực hiện thêm vào mapContacts
            JSONObject item = new JSONObject();
            JSONArray arrayEmail = new JSONArray();
            JSONArray arrayPhone = new JSONArray();
            String displayName = currentCursor.getString(currentCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            Cursor emails = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + idContact, null, null);

            while (emails.moveToNext()) {
                arrayEmail.put(emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
            }
            emails.close();
            Cursor pCur = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{idContact},
                    null);
            while (pCur.moveToNext()) {
                arrayPhone.put(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }
            pCur.close();
            item.put(ContactsContract.CommonDataKinds.Phone._ID, idContact);
            item.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, displayName);
            item.put(PHONE_KEY, arrayPhone);
            item.put(EMAIL_KEY, arrayEmail);
            data_upload.put(item);
        }
        return data_upload;

    }

    /**
     * Override lại hàm upload, không liên quan gì đến lớp cha hết
     */
    @Override
    public void upload() {
        try {
            JSONArray jsonUpload = getDataUpload();
            // Không có data để gửi lên, return luôn
            if (jsonUpload.length() <= 0)
                return;

            JSONObject jsonData = new JSONObject();
            PreferencesController preferencesController = new PreferencesController(mContext);
            String token = preferencesController.getToken();
            String childId = ChildModel.QueryHelper.getChildIdActive(mContext);

            // Nếu không tồn tại token
            // thì không thực hiện gì cả
            if (token == null || childId == null) {
                Log.d("mc_log", "ContactResource: token == null || childId == null");
                return;
            }
            String version_client = VersionUtils.updateVersion(mContext, VersionModel.Contents.TABLE_NAME, childId);

            jsonData.put(UrlPattern.TOKEN_KEY, token);
            jsonData.put(UrlPattern.IMEI_KEY, DeviceInfoUtils.getImei(mContext));
            jsonData.put(UrlPattern.DEVICE_NAME_KEY, DeviceInfoUtils.getDeviceName());
            jsonData.put(UrlPattern.CHILD_ID_KEY, childId);
            jsonData.put(UrlPattern.DATA_KEY, jsonUpload);
            jsonData.put(UrlPattern.VERSION_KEY, version_client);
            String url = UrlPattern.HOST + UrlPattern.API_VERSION + this.nameResource;
            Log.d("mc_log", "ContactResource:" + url);
            String respond = HttpConnection.exePostConnection(new URL(url), jsonData);

            if (respond == null || respond.length() == 0) {

            } else {
                JSONObject jsonRespond = new JSONObject(respond);
                if (jsonRespond.getInt(UrlPattern.STATUS_KEY) == 1 &&
                        jsonRespond.getString(UrlPattern.MSG_KEY) == UrlPattern.STATUS_DB.get("insert_success")) {
                    deleteDataClient();
                    VersionUtils.updateVersion(mContext, table_name, childId);
                } else if (jsonRespond.getInt(UrlPattern.STATUS_KEY) == 0) {

                    String msg = jsonRespond.getString(UrlPattern.MSG_KEY);
                    if (UrlPattern.ERROR_AUTH.equals(msg)) {
                        AccountResource.setLogout(mContext);
                    } else {

                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Override lại hàm download, không liên quan gì đến lớp cha hết
     *
     * @param
     * @return
     * @throws MalformedURLException
     * @throws JSONException
     */


    @Override
    public String download()  {
        PreferencesController preferencesController = new PreferencesController(mContext);
        String token = preferencesController.getToken();

        String childId = ChildModel.QueryHelper.getChildIdActive(mContext);
        String deviceId = ChildModel.QueryHelper.getDeviceIdChildActive(mContext);
        // Nếu không tồn tại token thì không thực hiện gì cả
        if (token == null || childId == null || deviceId == null) {
            AccountResource.setLogout(mContext);
            return "error_2";
        }
        String imei = DeviceInfoUtils.getImei(mContext);
        String deviceName = DeviceInfoUtils.getDeviceName();

        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(UrlPattern.TOKEN_KEY, token);
        hashMap.put(UrlPattern.IMEI_KEY, imei);
        hashMap.put(UrlPattern.DEVICE_NAME_KEY, deviceName);
        hashMap.put(UrlPattern.CHILD_ID_KEY, childId);
        hashMap.put(UrlPattern.DEVICE_ID_KEY, deviceId);

        String url = UrlPattern.HOST + UrlPattern.API_VERSION + this.nameResource;

        String respondText = HttpConnection.exeGetConnection(url, hashMap);

        if (respondText == null || respondText.length() == 0) {
        } else {
            JSONObject jsonRespond = null;
            try {
                jsonRespond = new JSONObject(respondText);
                if (jsonRespond.getInt(UrlPattern.STATUS_KEY) == 1) {
                    new PreferencesController(mContext).putLatestDownloadContact(System.currentTimeMillis());
                    // Xóa hết bang email, bang phone, bang number
                    mContext.getContentResolver().delete(ContactModel.Contents.CONTENT_URI, null, null);
                    mContext.getContentResolver().delete(EmailModel.Contents.CONTENT_URI, null, null);
                    mContext.getContentResolver().delete(EmailModel.Contents.CONTENT_URI, null, null);

                    JSONArray data = jsonRespond.getJSONArray(UrlPattern.DATA_KEY);
                    JSONObject item;
                    for (int i = 0; i < data.length(); i++) {
                        item = data.getJSONObject(i);
                        item.remove(UrlPattern.ACTION_KEY);
    //                    String version = item.getString(UrlPattern.VERSION_KEY);
    //                    item.remove(UrlPattern.VERSION_KEY);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ContactModel.Contents.ID_CHILD, childId);
                        contentValues.put(ContactModel.Contents.IS_BACKUP, Constant.BACKUP_TRUE);

                        String id = item.getString((ContactModel.Contents._ID));
                        String display_name = item.getString(ContactModel.Contents.DISPLAY_NAME);
                        JSONArray emailArray = item.getJSONArray(EmailModel.Contents.TABLE_NAME);
                        JSONArray phoneArray = item.getJSONArray(PhoneModel.Contents.TABLE_NAME);


                        contentValues.put(ContactModel.Contents.DISPLAY_NAME, display_name);
                        contentValues.put(ContactModel.Contents._ID, id);

                        mContext.getContentResolver().insert(
                                ContactModel.Contents.CONTENT_URI,
                                contentValues
                        );
                        for (int emailIndex = 0; emailIndex < emailArray.length(); emailIndex++) {
                            String email = (String) emailArray.get(emailIndex);
                            contentValues = new ContentValues();
                            contentValues.put(EmailModel.Contents._ID, id);
                            contentValues.put(EmailModel.Contents.ADDRESS, email);
                            mContext.getContentResolver().insert(
                                    EmailModel.Contents.CONTENT_URI,
                                    contentValues
                            );
                        }

                        for (int phoneIndex = 0; phoneIndex < phoneArray.length(); phoneIndex++) {
                            String phone = (String) phoneArray.get(phoneIndex);
                            contentValues = new ContentValues();
                            contentValues.put(PhoneModel.Contents._ID, id);
                            contentValues.put(PhoneModel.Contents.NUMBER, phone);
                            mContext.getContentResolver().insert(
                                    PhoneModel.Contents.CONTENT_URI,
                                    contentValues
                            );
                        }
                    }
                } else {
                    String error_id = jsonRespond.getString(UrlPattern.MSG_KEY);
                    if (error_id.equals(UrlPattern.ERROR_AUTH)) {
                        AccountResource.setLogout(mContext);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}


//
//    @Override
//    protected JSONArray getDataUpload() throws JSONException {
//        // point.x lưu id của contact, point.y lưu version của contact
//        // Danh sách version được lưu trong csdl của app
//        ArrayList<Point> beforeVersion = new ArrayList<>();
//        ContentResolver resolver = mContext.getContentResolver();
//
//        // Thực hiện query csdl của app
//        Cursor beforeCursor = resolver.query(ContactModel.Contents.CONTENT_URI, null, null, null, ContactModel.Contents._ID + " asc");
//
//        /**
//         * Biến isBackup chỉ kiểm tra xem là đã upload lần nào chưa
//         * - isBackup = False Nếu đây là lần đầu tiên, không cần so sánh với bản sao lưu trong database của mình
//         * - isBackup = True Còn ngược lại , đã được upload ít nhất 1 lần rồi thì so sánh với bản local
//         */
//        int isBackup = Constant.BACKUP_FALSE;
//
//        // Lưu id và version của mỗi contact vào trong beforeVersion
//        while (beforeCursor.moveToNext()) {
//            int idContact = beforeCursor.getInt(beforeCursor.getColumnIndex(ContactModel.Contents._ID));
//            int version = beforeCursor.getInt(beforeCursor.getColumnIndex(ContactModel.Contents.VERSION));
//            beforeVersion.add(new Point(idContact, version));
//            isBackup = beforeCursor.getInt(beforeCursor.getColumnIndex(ContactModel.Contents.IS_BACKUP));
//        }
//
//        // TƯƠNG TỤ, lưu id và version của mỗi contact vào mảng currentVersion
//        ArrayList<Point> currentVersion = new ArrayList<>();
//        // mapContacts lưu thông tin contacts hiện tại
//        // format: key là id,json là thông tin display_name,email,phone
//        HashMap<Integer, JSONObject> mapContacts = new HashMap<>();
//
//        getContactCurrent(mapContacts, currentVersion);
//
//        JSONArray jsonUpload;
//        /**
//         * Array các element format
//         * Element format
//         * {
//         *      "display":[]
//         *      "phone":[],
//         *      "email":[],
//         *      "action": delete/add/update
//         * }
//         */
//
//        if (isBackup == Constant.BACKUP_FALSE) {
//            jsonUpload = new JSONArray();
//            // Nếu chưa được upload lên lần nào
//            for (JSONObject item : mapContacts.values()) {
//                item.put(ACTION_KEY, ACTION_ADD);
//                jsonUpload.put(item);
//            }
//        } else {
//            jsonUpload = mergerData(mapContacts, beforeVersion, currentVersion);
//        }
//        return jsonUpload;
//    }
//
//    public ContactResource(Context context) {
//        this.nameResource = "contact";
//        this.table_name = ContactModel.Contents.TABLE_NAME;
//        this.uri = ContactModel.Contents.CONTENT_URI;
//        this.projections = new String[]{
//                PhoneModel.Contents.NUMBER,
//                EmailModel.Contents.ADDRESS,
//        };
//        this.mContext = context;
//    }
//
//    /**
//     * Override lại hàm upload
//     */
//    @Override
//    public void upload() {
//        try {
//            JSONArray jsonUpload = getDataUpload();
//            // Không có data để gửi lên, return luôn
//            if (jsonUpload.length() <= 0)
//                return;
//
//            JSONObject jsonData = new JSONObject();
//            String token = new PreferencesController(mContext).getToken();
//
//            // Nếu không tồn tại token
//            // thì không thực hiện gì cả
//            if (token == null) {
//                return;
//            }
//
//            jsonData.put(UrlPattern.TOKEN_KEY, token);
//            jsonData.put(UrlPattern.IMEI_KEY, DeviceInfoUtils.getImei(mContext));
//            jsonData.put(UrlPattern.DEVICE_NAME_KEY, DeviceInfoUtils.getDeviceName());
//            jsonData.put(UrlPattern.DATA_KEY, jsonUpload);
//            String url = UrlPattern.HOST + UrlPattern.API_VERSION + this.nameResource;
//            String respond = HttpConnection.exePostConnection(new URL(url), jsonData);
//
//            if (respond == null || respond.length() == 0) {
//
//            } else {
//                JSONObject jsonRespond = new JSONObject(respond);
//                if (jsonRespond.getInt(UrlPattern.STATUS_KEY) == 1 &&
//                        jsonRespond.getString(UrlPattern.MSG_KEY) == UrlPattern.STATUS_DB.get("insert_success")) {
//                    deleteDataClient();
//                    VersionUtils.updateVersion(mContext, table_name);
//                } else if (jsonRespond.getInt(UrlPattern.STATUS_KEY) == 0) {
//
//                    String msg = jsonRespond.getString(UrlPattern.MSG_KEY);
//                    if (UrlPattern.ERROR_AUTH.equals(msg)) {
//                        AccountResource.setLogout(mContext);
//                    } else {
//
//                    }
//                }
//            }
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * @param mapContacts
//     * @param beforeVersion
//     * @param currentVersion
//     * @return
//     * @throws JSONException
//     */
//
//    private JSONArray mergerData(HashMap<Integer, JSONObject> mapContacts, ArrayList<Point> beforeVersion, ArrayList<Point> currentVersion) throws JSONException {
//        JSONArray data = new JSONArray();
//
//        /**
//         * Bắt đầu thuật toán xo khớp giữa 2 version
//         */
//        int beforeIndex = 0, currentIndex = 0;
//        while (beforeIndex < beforeVersion.size() || currentIndex < currentVersion.size()) {
//            //
//            int idCu = beforeVersion.get(currentIndex).x;
//            int verCu = beforeVersion.get(currentIndex).y;
//
//            int idBe = beforeVersion.get(beforeIndex).x;
//            int verBe = beforeVersion.get(beforeIndex).y;
//
//            JSONObject content = mapContacts.get(idCu);
//            if (idCu == idBe) {
//                if (verCu != verBe) {
//                    // version cũ khác version mới là update
//
//                    content.put(ACTION_KEY, ACTION_UPDATE);
//
//                } else {
//                    // Trường hợp bằng nghĩa là không thay đổi,
//                    // Không cần gửi gửi gì lên server
//
//                }
//                currentIndex++;
//                beforeIndex++;
//
//            } else if (idCu > idBe) {
//                JSONObject jsonDel = new JSONObject();
//                jsonDel.put(ID_KEY, idBe);
//                content.put(ACTION_KEY, ACTION_DELETE);
//                beforeIndex++;
//
//
//            } else if (idCu < idBe) {
//                content.put(ACTION_KEY, ACTION_ADD);
//                currentIndex++;
//            }
//            data.put(content);
//        }
//
//        while (beforeIndex < beforeVersion.size()) {
//            int idBe = beforeVersion.get(beforeIndex).x;
//            JSONObject jsonDel = new JSONObject();
//            jsonDel.put(ID_KEY, idBe);
//            jsonDel.put(ACTION_KEY, ACTION_ADD);
//            data.put(jsonDel);
//            beforeIndex++;
//        }
//
//        while (currentIndex < currentVersion.size()) {
//            int idCu = currentVersion.get(currentIndex).x;
//            JSONObject jsonAdd = mapContacts.get(idCu);
//            jsonAdd.put(ACTION_KEY, ACTION_ADD);
//            data.put(jsonAdd);
//            currentIndex++;
//        }
//        return data;
//    }
//
//
//    /**
//     * Thực hiện lấy dữ liệu
//     *
//     * @param mapContacts
//     * @param currentVersion
//     * @throws JSONException
//     */
//    private void getContactCurrent(HashMap<Integer, JSONObject> mapContacts, ArrayList<Point> currentVersion) throws JSONException {
//        // Thực hiện truy vấn cơ sở dữ liệu hiện tại sắp xếp id tăng dần
//        ContentResolver resolver = mContext.getContentResolver();
//        Cursor currentCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
//                null, null, null, ContactModel.Contents._ID + " asc");
//
//        while (currentCursor.moveToNext()) {
//
//            int idContact = currentCursor.getInt(currentCursor.getColumnIndex(ContactModel.Contents._ID));
//            Cursor cVersion = resolver.query(ContactsContract.RawContacts.CONTENT_URI,
//                    null,
//                    ContactsContract.RawContacts.CONTACT_ID + " = " + idContact,
//                    null,
//                    null);
//            cVersion.moveToFirst();
//            int version = cVersion.getInt(cVersion.getColumnIndex(ContactsContract.RawContacts.VERSION));
//            cVersion.close();
//            currentVersion.add(new Point(idContact, version));
//
//            // Thực hiện thêm vào mapContacts
//            JSONObject item = new JSONObject();
//            JSONArray arrayEmail = new JSONArray();
//            JSONArray arrayPhone = new JSONArray();
//            String displayName = currentCursor.getString(currentCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//            Cursor emails = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + idContact, null, null);
//
//            while (emails.moveToNext()) {
//                arrayEmail.put(emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
//            }
//            emails.close();
//            Cursor pCur = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                    null,
//                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                    new String[]{String.valueOf(idContact)},
//                    null);
//            while (pCur.moveToNext()) {
//                arrayPhone.put(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//            }
//            pCur.close();
//            item.put(ContactsContract.CommonDataKinds.Phone._ID, idContact);
//            item.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, displayName);
//            item.put(PHONE_KEY, arrayPhone);
//            item.put(EMAIL_KEY, arrayEmail);
//            mapContacts.put(idContact, item);
//        }
//    }
//}
