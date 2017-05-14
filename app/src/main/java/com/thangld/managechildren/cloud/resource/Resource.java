package com.thangld.managechildren.cloud.resource;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.SmsModel;
import com.thangld.managechildren.storage.model.VersionModel;
import com.thangld.managechildren.utils.VersionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.HashMap;

import static com.thangld.managechildren.cloud.JsonParse.fromCursor;

/**
 * Created by thangld on 15/04/2017.
 */

public abstract class Resource {

    protected String nameResource;
    /**
     * Uri provider
     */
    protected Uri uri;
    protected String table_name;
    protected Context mContext;
    /**
     * Những trường khi query
     */
    protected String[] projections;


    protected String versionClient;

    public Resource() {

    }

    public Resource(String nameResource, Uri uri, Context context, String[] projections) {
        this.nameResource = nameResource;
        this.uri = uri;
        this.mContext = context;
        this.projections = projections;
    }

    /**
     * @return
     */
    protected JSONArray getDataUpload() throws JSONException {
        Cursor cursor = mContext.getContentResolver().query(
                this.uri,
                this.projections,
                "is_backup = ?",
                new String[]{String.valueOf(Constant.BACKUP_FALSE)},
                null
        );
        // Mặc định json
        return fromCursor(cursor);
    }

    /**
     * Khi thực hiện upload thành công rồi thì xóa những bản ghi
     * trong cơ sở dữ liệu
     */

    protected void deleteDataClient() {
        mContext.getContentResolver().delete(
                this.uri,
                "is_backup = ?",
                new String[]{String.valueOf(Constant.BACKUP_FALSE)}
        );
    }

    /**
     * Lấy version ở client
     *
     * @return
     */

    /**
     * Lấy dữ liệu và thực hiện upload
     *
     * @throws JSONException
     */
    public void upload() {
        try {
            JSONArray array_data = getDataUpload();
            Log.d("mc_log", "\n\nUPLOAD: " + this.nameResource);
            // Không có data để gửi lên, return luôn
            if (array_data.length() <= 0) {
                Log.d("mc_log", "onUpload(): nothing to upload");
                return;
            }
            Log.d("mc_log", "Resource-upload: " + array_data.toString());

            // Lấy version hiện tại của server
            String data = new VersionResource(mContext).getVersionServer(nameResource, String.valueOf(UrlPattern.PRIVILEGE_CHILD));
            if (data == null) {
                return;
            }
            JSONObject versionRespond = new JSONObject(data);
            String versionServer = "";
            if (versionRespond.getInt(UrlPattern.STATUS_KEY) == 1) {
                versionServer = versionRespond.getString(UrlPattern.MSG_KEY);
                Log.d("mc_log", "upload() " + this.nameResource + " version-server " + versionServer);
            } else {
                Log.d("mc_log", "upload() " + this.nameResource + " fail to get version server");
                // Nếu không lấy được version server
                // Không thực hiện gửi nữa
                return;
            }

            // Lấy version hiện tại trên client
            String childId = ChildModel.QueryHelper.getChildIdActive(mContext);
            versionClient = VersionModel.VersionHelper.getNumberVersion(mContext, childId, table_name);
            Log.d("mc_log", this.nameResource + "onUpload() versionClient-" + versionClient);
            // Chỉ thực hiện upload khi version client lớn hơn versin server
            if (VersionUtils.compareVersion(versionClient, versionServer) > 0) {
                JSONObject dataUpload = new JSONObject();
                dataUpload.put(UrlPattern.DATA_KEY, array_data);
                dataUpload.put(UrlPattern.VERSION_KEY, versionClient);
                dataUpload.put(UrlPattern.CHILD_ID_KEY, childId);
                // Thực hiện upload
                // 1 là up list json
                // 2 là up file

                exeUpload(dataUpload);
            } else {
                Log.d("mc_log", "upload" + this.nameResource + "db up to date");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract void onUploadFinish(String respond) throws JSONException;

    /**
     * Thực hiện check version,
     * download khi version client bé hơn client server
     */
    public String download() {
        String respond = null;
        try {
            // Lấy version hiện tại của server
            String data = null;
            data = new VersionResource(mContext).getVersionServer(nameResource, String.valueOf(UrlPattern.PRIVILEGE_PARENT));
            if (data == null) {
                // Do ko co child_id
                return null;
            }
            JSONObject versionRespond = new JSONObject(data);
            String versionServer = "";
            if (versionRespond.getInt(UrlPattern.STATUS_KEY) == 1) {
                versionServer = versionRespond.getString(UrlPattern.MSG_KEY);
            } else {
                String error_id = null;
                error_id = versionRespond.getString(UrlPattern.MSG_KEY);
                if (UrlPattern.ERROR_AUTH.equals(error_id)) {
                    AccountResource.setLogout(mContext);
                }
                // Nếu không lấy được version server
                // Không thực hiện gửi nữa
                return null;
            }
            // Lấy version hiện tại trên client
            String childId = ChildModel.QueryHelper.getChildIdActive(mContext);
            versionClient = VersionModel.VersionHelper.getNumberVersion(mContext, childId, table_name);

            if (versionClient == null) {
                versionClient = "0.0.0.0";
            }
            Log.d("mc_log", "versionClient " + versionClient);

            // Chỉ thực hiện download khi version client bé hơn versin server
            if (VersionUtils.compareVersion(versionClient, versionServer) < 0) {
                Log.d("mc_log", "compareVersion versionServer > versionClient");

                // Thực hiện upload
                // 1 là up list json
                // 2 là up file

                Cursor cursor = mContext.getContentResolver().query(uri,
                        new String[]{SmsModel.Contents.ID_SERVER}, null, null, null);
                JSONArray ids = new JSONArray();

                // chuyen key "id_server" thanh "_id"
                if (cursor != null & cursor.moveToNext()) {
                    do {

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(SmsModel.Contents._ID, cursor.getString(cursor.getColumnIndex(SmsModel.Contents.ID_SERVER)));
                        ids.put(jsonObject);
                    } while (cursor.moveToNext());
                }
                Log.d("mc_log", "download " + this.nameResource + ids.toString());
                JSONObject dataUpload = new JSONObject();
                dataUpload.put(UrlPattern.DATA_KEY, ids);
                dataUpload.put(UrlPattern.VERSION_KEY, versionClient);
                // Xử lý ở exeDownload tất cả liên quan đến tất cả
                return exeDownload(dataUpload);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param data
     */
    public abstract String exeUpload(JSONObject data) throws JSONException;

    /**
     * Thực hiện download
     *
     * @param dataUpload
     * @return
     */
    public abstract String exeDownload(JSONObject dataUpload) throws MalformedURLException, JSONException;

    public abstract boolean isRecordExist(HashMap<String, String> hashMap);

}
