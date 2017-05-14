package com.thangld.managechildren.main.parent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thangld.managechildren.Constant;
import com.thangld.managechildren.R;
import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.storage.controller.BackupFolder;
import com.thangld.managechildren.storage.controller.DatabaseHelper;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.VideoModel;
import com.thangld.managechildren.utils.NetworkUtils;

import java.io.File;

import static java.io.File.separator;

/**
 * Created by thangld on 20/04/2017.
 */

public class VideoFragment extends ListNavContentFragment {

    private File[] mFileArray;
    private ImageListAdapter mAdapter;
    File dirs;

    public class ImageItem {
        public String nameFile;
        public String pathFile;
    }

    @Override
    protected void setAdapterContent() {
        // chuyển thành 2 cột
        listViewContainer.setNumColumns(2);

        String childId = ChildModel.QueryHelper.getChildIdActive(mContext);
        if (childId != null) {
            //        mCursor = mContext.getContentResolver().query(SmsModel.Contents.CONTENT_URI, null, null, null, null);
            DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
            String cmdSql =
                    "SELECT * FROM " + VideoModel.Contents.TABLE_NAME + " "
                            + "WHERE " + VideoModel.Contents.ID_CHILD + "= '" + childId + "' "
                            + "ORDER BY " + VideoModel.Contents.DATE_ADDED + " DESC";

            mCursor = databaseHelper.getWritableDatabase().rawQuery(cmdSql, null);
            Log.d("mc_log", "SmsFragmen getCount()" + mCursor.getCount());


            // Dung cusor adapterl
//            mCursor.setNotificationUri(getContext().getContentResolver(), SmsModel.Contents.CONTENT_URI);
//            cursorAdapter = new ImageAdapter(mContext, mCursor, true);
//            cursorAdapter.swapCursor(mCursor);
//            listViewContainer.setAdapter(cursorAdapter);

            // DUng picasso

            String device_id = ChildModel.QueryHelper.getDeviceIdChildActive(mContext);
            String pathDir = Constant.STORAGE_DATA + separator +
                    BackupFolder.BACKUPS + separator + device_id + separator + childId + separator + BackupFolder.VIDEO;
            Log.d("mc_log", pathDir);

            dirs = new File(pathDir);
            if (dirs.exists()) {
                mFileArray = dirs.listFiles();
            } else {
                mFileArray = new File[0];
            }

            mAdapter = new ImageListAdapter(mContext, mFileArray);
            listViewContainer.setAdapter(mAdapter);
            listViewContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String pathFile = mFileArray[i].getAbsolutePath();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + pathFile), "video/*");
                    startActivity(intent);
                }
            });

        }
    }

    @Override
    protected void checkOnline() {
        // TODO KHông phải khi nào vào cũng check online như thế này

        if (NetworkUtils.isNetworkConnected(mContext)) {
            TransferService.startActionDownload(mContext, TransferService.DOWNLOAD_VIDEO);
        } else {
            // Hiển thị thanh thông báo không có kết nói
        }


    }

    @Override
    protected void setClickListeners() {
    }



    public class ImageListAdapter extends ArrayAdapter {
        private Context context;
        private LayoutInflater inflater;

        private File[] files;

        public ImageListAdapter(Context context, File[] files) {
            super(context, R.layout.item_media, files);

            this.context = context;
            this.files = files;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_media, parent, false);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_preview);
            TextView title = (TextView) convertView.findViewById(R.id.display_name);

            title.setText(files[position].getName());
            Picasso.with(context)
                    .load(files[position])
                    .into(imageView);

            return convertView;
        }
    }
}
