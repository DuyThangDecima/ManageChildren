package com.thangld.managechildren.cloud.resource;

import android.content.Context;

import com.thangld.managechildren.storage.model.AudioModel;

import org.json.JSONObject;

/**
 * Created by thangld on 15/04/2017.
 */

public class AudioResource extends FileResource {

    public static final String AUDIO_RESOURCE = "audio";

    public AudioResource(Context context) {
        this.uri = AudioModel.Contents.CONTENT_URI;
        this.nameResource = AUDIO_RESOURCE;
        this.projections = new String[]{
                AudioModel.Contents._ID,
                AudioModel.Contents.DATA,
                AudioModel.Contents.DURATION,
                AudioModel.Contents.DATE_ADDED,
                AudioModel.Contents.DISPLAY_NAME,
                AudioModel.Contents.SIZE,
        };
        this.table_name = "audio";
        this.mContext = context;
    }


    @Override
    public String exeUpload(JSONObject jsonParams) {
        return super.exeUpload(jsonParams);
    }
}
