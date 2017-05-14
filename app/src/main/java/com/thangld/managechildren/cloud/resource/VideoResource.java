package com.thangld.managechildren.cloud.resource;

import android.content.Context;

import com.thangld.managechildren.storage.model.VideoModel;

/**
 * Created by thangld on 15/04/2017.
 */

public class VideoResource extends FileResource {

    public static final String VIDEO_RESOURCE = "video";

    public VideoResource(Context context) {
        this.uri = VideoModel.Contents.CONTENT_URI;
        this.nameResource = VIDEO_RESOURCE;
        this.projections = new String[]{
                VideoModel.Contents._ID,
                VideoModel.Contents.DATA,
                VideoModel.Contents.DISPLAY_NAME,
                VideoModel.Contents.DATE_ADDED,
                VideoModel.Contents.DURATION,
                VideoModel.Contents.SIZE,
        };
        this.table_name = "video";
        this.mContext = context;
    }


}
