package com.thangld.managechildren.cloud.resource;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.cloud.HttpConnection;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.storage.controller.BackupFolder;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.ImageModel;
import com.thangld.managechildren.utils.DeviceInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import static java.io.File.separator;

/**
 * Created by thangld on 19/04/2017.
 */

public class FileResource extends Resource implements MethodFileResource {

    public FileResource(String nameResource, Uri uri, Context context, String[] projections) {
        super(nameResource, uri, context, projections);
    }

    @Override
    public void onUploadFinish(String respond) throws JSONException {

    }

    public FileResource() {

    }

    @Override
    public String download() {
        return super.download();

    }


    @Override
    public String put(String id, String filePath, HashMap infor) {
        String respond = "";
        try {
            if (infor == null) {
                infor = new HashMap();
            }
            PreferencesController preferencesController = new PreferencesController(mContext);
            String token = preferencesController.getToken();
            // Nếu không tồn tại token thì không thực hiện gì cả
            if (token == null) {
                // Loi auth
                return "{\"status\":0, \"msg\":\"error_2\"}";
            }

            String imei = DeviceInfoUtils.getImei(mContext);
            String deviceName = DeviceInfoUtils.getDeviceName();
            infor.put(UrlPattern.TOKEN_KEY, token);
            infor.put(UrlPattern.IMEI_KEY, imei);
            infor.put(UrlPattern.DEVICE_NAME_KEY, deviceName);
            String url = UrlPattern.HOST + UrlPattern.API_VERSION + this.nameResource + "/" + id;
            respond = HttpConnection.exePutFileConnection(new URL(url), filePath, infor);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return respond;
    }

    /**
     * Gửi thông tin file lên server
     *
     * @param data
     * @return
     */
    @Override
    public String post(JSONObject data) {
        String errorDefault = "{\"status\":0, \"msg\":\"unknown\"}";

        try {
            String url = UrlPattern.HOST + UrlPattern.API_VERSION + this.nameResource;
            PreferencesController preferencesController = new PreferencesController(mContext);
            String token = preferencesController.getToken();
            // Nếu không tồn tại token thì không thực hiện gì cả
            if (token == null) {
                // Loi auth
                return "{\"status\":0, \"msg\":\"error_2\"}";
            }
            String imei = DeviceInfoUtils.getImei(mContext);
            String deviceName = DeviceInfoUtils.getDeviceName();
            if (data == null) {
                data = new JSONObject();
            }
            data.put(UrlPattern.TOKEN_KEY, token);
            data.put(UrlPattern.IMEI_KEY, imei);
            data.put(UrlPattern.DEVICE_NAME_KEY, deviceName);
            return HttpConnection.exePostConnection(new URL(url), data);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return errorDefault;
    }


    /**
     * @param jsonArray
     * @return
     */
    @Override
    public String post(JSONArray jsonArray) {
        return null;
    }


    @Override
    public String exeUpload(JSONObject jsonParams) {
        try {
            JSONArray array_data = jsonParams.getJSONArray(UrlPattern.DATA_KEY);
            String version_client = jsonParams.getString(UrlPattern.VERSION_KEY);
            JSONObject jsonData;
            JSONObject jsonInfoFile;
            String respondCreate, respondPut;
            JSONObject jsonRespondCreate, jsonRespondPut, jsonInfoFileToSend;
            for (int i = 0; i < array_data.length(); i++) {
                jsonData = new JSONObject();
                jsonInfoFile = array_data.getJSONObject(i);
                /**
                 * Không lấy trường data.
                 */
//                jsonInfoFileToSend = jsonInfoFile;
                String file_path = jsonInfoFile.getString("data");
                int _id = jsonInfoFile.getInt("_id");

                jsonInfoFile.remove("data");
                jsonInfoFile.remove("_id");


                jsonData.put(UrlPattern.INFO_FILE_KEY, jsonInfoFile);
                jsonData.put(UrlPattern.VERSION_KEY, version_client);
                jsonData.put(UrlPattern.CHILD_ID_KEY, ChildModel.QueryHelper.getChildIdActive(mContext));

                // Thực hiện post, put từng file
                respondCreate = post(jsonData);

                jsonRespondCreate = new JSONObject(respondCreate);
                Log.d("mc_log", "jsonRespondCreate.getInt(UrlPattern.STATUS_KEY): " + jsonRespondCreate.getInt(UrlPattern.STATUS_KEY));
                if (jsonRespondCreate.getInt(UrlPattern.STATUS_KEY) == 1) {

                    Log.d("mc_log", "go to put file");

                    String id = jsonRespondCreate.getString(UrlPattern.ID_KEY);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(UrlPattern.VERSION_KEY, versionClient);
                    hashMap.put(UrlPattern.CHILD_ID_KEY, ChildModel.QueryHelper.getChildIdActive(mContext));

                    respondPut = put(id, file_path, hashMap);
                    if (respondPut == null || respondPut.length() == 0) {
                        return null;
                    }
                    jsonRespondPut = new JSONObject(respondPut);
                    if (jsonRespondPut.getInt(UrlPattern.STATUS_KEY) == 1) {
                        // Thực hiện xóa file
                        mContext.getContentResolver().delete(uri, ImageModel.Contents._ID + " = ?", new String[]{String.valueOf(_id)});
                        new File(file_path).delete();
                    } else {
                        String error_id = jsonRespondPut.getString(UrlPattern.MSG_KEY);
                        if (UrlPattern.ERROR_EXIST.equals(error_id)) {
                            // Server đã có file này rồi,xóa đi
                            mContext.getContentResolver().delete(uri, ImageModel.Contents._ID + " = ?", new String[]{String.valueOf(_id)});
                            new File(file_path).delete();
                        }
                    }
                } else {
                    continue;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String exeDownload(JSONObject dataUpload) throws MalformedURLException, JSONException {
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
        if (dataUpload == null) {
            dataUpload = new JSONObject();
        }
        dataUpload.put(UrlPattern.TOKEN_KEY, token);
        dataUpload.put(UrlPattern.IMEI_KEY, imei);
        dataUpload.put(UrlPattern.DEVICE_NAME_KEY, deviceName);
        dataUpload.put(UrlPattern.CHILD_ID_KEY, childId);
        dataUpload.put(UrlPattern.DEVICE_ID_KEY, deviceId);

        String urlGetFileNeedDownload = UrlPattern.HOST + UrlPattern.API_VERSION +
                UrlPattern.GET_FILES_NEED_DOWNLOAD + "/" + this.nameResource;
        String urlDownload = UrlPattern.HOST + UrlPattern.API_VERSION +
                UrlPattern.DOWNLOAD + "/" + this.nameResource;

        String respondText = HttpConnection.exePostConnection(new URL(urlGetFileNeedDownload), dataUpload);

        Log.d("mc_log", "respond GetFileNeedDownload " + this.nameResource + respondText);
        if (respondText == null || respondText.length() == 0) {

        } else {
            JSONObject jsonRespond = new JSONObject(respondText);
            if (jsonRespond.getInt(UrlPattern.STATUS_KEY) == 1) {
                // Khi nhan danh sach cac file download

                JSONArray data = jsonRespond.getJSONArray(UrlPattern.DATA_KEY);
                JSONObject item;
                for (int i = 0; i < data.length(); i++) {
                    // Thuc hien download tung file
                    item = data.getJSONObject(i);
                    String _id = item.getString(UrlPattern._ID_KEY);
                    JSONObject download = new JSONObject();

                    download.put(UrlPattern.FILE_ID_KEY, _id);
                    download.put(UrlPattern.TOKEN_KEY, token);
                    download.put(UrlPattern.IMEI_KEY, imei);
                    download.put(UrlPattern.DEVICE_NAME_KEY, deviceName);
                    download.put(UrlPattern.CHILD_ID_KEY, childId);
                    download.put(UrlPattern.DEVICE_ID_KEY, deviceId);

                    // TODO KHI NGUOI DUNG XOA
                    String action = item.getString(UrlPattern.ACTION_KEY);
                    item.remove(UrlPattern.ACTION_KEY);

                    String pathFile = Constant.STORAGE_DATA + separator +
                            BackupFolder.BACKUPS + separator + deviceId + separator + childId + separator + this.nameResource;

                    Log.d("mc_log", "pathFile" + pathFile);
                    File path = new File(pathFile);
                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    String nameFile = item.getString("display_name");
                    boolean result = HttpConnection.downloadFile(urlDownload, download, pathFile, nameFile);
                    Log.d("mc_log", "download result" + result);
                    if (result) {
                        // thêm vào db
                        ContentValues contentValues = new ContentValues();
                        // ĐỔi key _id thành id server
                        String _id_receive = item.getString(UrlPattern._ID_KEY);
                        item.remove(UrlPattern._ID_KEY);
                        item.put(ImageModel.Contents.ID_SERVER, _id_receive);

                        item.put(ImageModel.Contents.ID_CHILD, childId);
                        item.put(ImageModel.Contents.IS_BACKUP, Constant.BACKUP_TRUE);
                        item.put(ImageModel.Contents.DATA, pathFile);
                        Iterator<?> keys = item.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            // TODO check item.get(key).toString()
                            contentValues.put(key, item.get(key).toString());
                        }
                        mContext.getContentResolver().insert(uri, contentValues);
                    }
                }
            } else {
                String error_id = jsonRespond.getString(UrlPattern.MSG_KEY);
                if (error_id.equals(UrlPattern.ERROR_AUTH)) {
                    AccountResource.setLogout(mContext);
                }else if (error_id.equals(UrlPattern.ERROR_EXIST)) {
                    // xoa
                }

            }
        }
        return null;
    }

    @Override
    public boolean isRecordExist(HashMap<String, String> hashMap) {
        return false;
    }
}
