package com.thangld.managechildren.cloud.resource;

import android.content.Context;

/**
 * Created by thangld on 22/04/2017.
 */

public class ChildResource extends JsonResource {

    public static final String CHILD_RESOURCE = "child";

    public ChildResource(Context context) {
        this.nameResource = CHILD_RESOURCE;
        this.mContext = context;
    }
}
