package com.thangld.managechildren.cloud.resource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by thangld on 19/04/2017.
 */

public interface MethodFileResource {
    /**
     * Gui file
     * @param id
     * @return
     */
    String put(String id, String filePath, HashMap<String,String> auth);


    /**
     * request tao file tren server voi nhung thong tin nhu sau
     * @param jsonObject
     * @return
     */
    String post(JSONObject jsonObject);

    /***
     *
     */
    String post(JSONArray jsonArray);
}
