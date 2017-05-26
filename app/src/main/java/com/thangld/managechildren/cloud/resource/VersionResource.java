package com.thangld.managechildren.cloud.resource;

import android.content.Context;

import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.VersionModel;

import java.net.MalformedURLException;
import java.util.HashMap;

/**
 * Created by thangld on 15/04/2017.
 */

public class VersionResource extends JsonResource {

    /**
     * version
     */
    public static final String VERSION_RESOURCE = "version";


    public VersionResource(Context context) {
        this.nameResource = VERSION_RESOURCE;
        this.table_name = VersionModel.Contents.TABLE_NAME;
        this.uri = VersionModel.Contents.CONTENT_URI;
        this.projections = new String[]{
                VersionModel.Contents.TABLE_NAME,
                VersionModel.Contents.NUMBER_VERSION,
        };
        this.mContext = context;
    }


    public String getVersionServer(String type) throws MalformedURLException {
        String child_id = ChildModel.QueryHelper.getChildIdActive(mContext);
        if (child_id == null) {
            return null;
        }
        int privilege = new PreferencesController(mContext).getPrivilege();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(UrlPattern.TYPE_VERSION_KEY, type);
        hashMap.put(UrlPattern.CHILD_ID_KEY, child_id);
        hashMap.put(UrlPattern.PRIVILEGE_KEY, String.valueOf(privilege));
        if (privilege == PreferencesController.PRIVILEGE_PARENT) {
            // Nếu là cha mẹ, thì phải thêm device_id nữa
            hashMap.put(UrlPattern.DEVICE_ID_KEY, ChildModel.QueryHelper.getDeviceIdChildActive(mContext));
        }
        return get(hashMap);
    }
}
