package com.thangld.managechildren.collector.observer;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.thangld.managechildren.Debug;
import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.cloud.resource.ContactResource;
import com.thangld.managechildren.collector.reader.AppReader;
import com.thangld.managechildren.collector.reader.AudioReader;
import com.thangld.managechildren.collector.reader.CallLogReader;
import com.thangld.managechildren.collector.reader.ImageReader;
import com.thangld.managechildren.collector.reader.SmsReader;
import com.thangld.managechildren.collector.reader.VideoReader;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.utils.NetworkUtils;

public class ObserverService extends Service {
    public static final String TAG = "ObserverService";
    public static final String TYPE_EXTRA = "type_extra";
    public static final String TYPE_BOOT_TIME = "type_boot_time";
    public static final String TYPE_CHILD_ENABLE_FIRST = "type_child_enable_first";


    private ContentResolver mContentResolver;

    private ImageObserver mImageObserver;
    private VideoObserver mVideoObserver;
    private SmsObserver mSmsObserver;
    private CallLogObserver mCallLogObserver;
    private ContactObserver mContactObserver;
    private AudioObserver mAudioObserver;

    private Context mContext;

    public ObserverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mContext = this;
        mContentResolver = getContentResolver();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(intent != null){
            String action  = intent.getAction();
            if (TYPE_BOOT_TIME.equals(action)) {

            } else if (TYPE_CHILD_ENABLE_FIRST.equals(action)) {
                readInStorage();
            }

        }
        registerObserver();

        /**
         *
         *  Nếu là lần đầu enable tài khoản của trẻ con này thì chắc chắn tgian sẽ lớn hơn 24 * 60 * 60 * 1000
         *  Nếu service bị tắt hoặc bị dừng (vì lý do nào đấy), thì check thời gian để gửi
         *
         */

        PreferencesController preferences = new PreferencesController(this);

        Log.d("mc_log", "ObserverService" + "UPLOAD ALL");
        if (NetworkUtils.isNetworkConnected(this)) {
            TransferService.startActionUpload(this, TransferService.UPLOAD_ALL);
        }

        return START_STICKY;
    }

    private void readInStorage() {

        //
        AppReader appReader = new AppReader(this);
        appReader.execute();

        Log.d("mc_log", "readInStorage");
        // 0
        ImageReader imageReader = new ImageReader(this);
        imageReader.execute();

        //1
        VideoReader videoReader = new VideoReader(this);
        videoReader.execute();
        //2
        AudioReader audioReader = new AudioReader(this);
        audioReader.execute();
        //3
        SmsReader smsReader = new SmsReader(this);
        smsReader.execute();
        //4
//        PhoneReader phoneReader = new PhoneReader(this);
//        phoneReader.execute();
        //5
//        EmailReader emailReader = new EmailReader(this);
//        emailReader.execute();
        //6
//        ContactReader contactReader = new ContactReader(this);
//        contactReader.execute();
        //7
        CallLogReader callLogReader = new CallLogReader(this);
        callLogReader.execute();

        //8


        new Thread(new Runnable() {
            @Override
            public void run() {
                new ContactResource(mContext).upload();
            }
        }).start();       // Upload

        Log.d("mc_log", "end readInStorage()");


    }

    private void registerObserver() {
        mImageObserver = new ImageObserver(new Handler(), this);
        mContentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, mImageObserver);

        mVideoObserver = new VideoObserver(new Handler(), this);
        mContentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, mVideoObserver);

        mAudioObserver = new AudioObserver(new Handler(), this);
        mContentResolver.registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mAudioObserver);

        mSmsObserver = new SmsObserver(new Handler(), this);
        mContentResolver.registerContentObserver(Uri.parse("content://sms/"), true, mSmsObserver);

        mCallLogObserver = new CallLogObserver(new Handler(), this);
        mContentResolver.registerContentObserver(Uri.parse("content://call_log/calls"), true, mCallLogObserver);

        mContactObserver = new ContactObserver(new Handler(), this);
        mContentResolver.registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mContactObserver);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Debug.logD(TAG, "onDestroy()");
        /**
         * Hủy lắng nghe
         */
        if (mImageObserver != null) {
            mContentResolver.unregisterContentObserver(mImageObserver);
        }

        if (mVideoObserver != null) {
            mContentResolver.unregisterContentObserver(mVideoObserver);
        }
        if (mSmsObserver != null) {
            mContentResolver.unregisterContentObserver(mSmsObserver);
        }
        if (mSmsObserver != null) {
            mContentResolver.unregisterContentObserver(mSmsObserver);
        }

        if (mContactObserver != null) {
            mContentResolver.unregisterContentObserver(mContactObserver);
        }

    }
}
