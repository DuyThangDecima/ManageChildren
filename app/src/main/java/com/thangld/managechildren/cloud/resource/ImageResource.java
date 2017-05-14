package com.thangld.managechildren.cloud.resource;

import android.content.Context;

import com.thangld.managechildren.storage.model.ImageModel;

/**
 * Created by thangld on 15/04/2017.
 */

public class ImageResource extends FileResource {

    public static final String IMAGE_RESOURCE = "image";

    public ImageResource(Context context) {
        this.uri = ImageModel.Contents.CONTENT_URI;
        this.nameResource = IMAGE_RESOURCE;
        this.projections = new String[]{
                ImageModel.Contents._ID,
                ImageModel.Contents.DATA,
                ImageModel.Contents.DISPLAY_NAME,
                ImageModel.Contents.DATE_ADDED,
                ImageModel.Contents.SIZE,
        };
        this.table_name = "image";
        this.mContext = context;
    }

}
