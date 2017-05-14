package com.thangld.managechildren.cloud.resource;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by thangld on 19/04/2017.
 */

public interface MethodJsonResource {

    String get(int id, HashMap<String, String> map);

    String get(HashMap<String, String> map);

    String post(JSONObject data);

    String put(JSONObject data);

}
