package com.thangld.managechildren.fcm;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.thangld.managechildren.R;
import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.location.AppLocationManager;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.LocationModel;
import com.thangld.managechildren.utils.NetworkUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";

    private static final String REQUEST_LOCATION = "request_location";
    private static final String RESPOND_LOCATION = "respond_location";

    private static final String REQUEST_UPDATE_RULE_PARENT = "request_update_rule_parent";

    private static final String REQUEST_UPLOAD = "request_upload";
    private static final String TYPE_REQUEST = "type_request";

    private Context mContext;

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        mContext = getApplicationContext();
        Map<String, String> data = remoteMessage.getData();
        Log.d(TAG, "Data" + data.toString());
        // Check if message contains a data payload.
        if (data.size() > 0) {
            String type_request = data.get(TYPE_REQUEST);

            if (REQUEST_LOCATION.equals(type_request)) {
                Log.d(TAG, "REQUEST_LOCATION");
                String device_id = data.get(UrlPattern.DEVICE_ID_KEY);
                Intent intent = new Intent(this, AppLocationManager.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(device_id);
                startService(intent);
            } else if (RESPOND_LOCATION.equals(type_request)) {
                String latLo = data.get(UrlPattern.LAT_LOCATION_KEY);
                String longLo = data.get(UrlPattern.LONG_LOCATION_KEY);


                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());
                String addressFull = "";
                try {
                    addresses = geocoder.getFromLocation(Double.valueOf(latLo), Double.valueOf(longLo), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    // lấy thông tin về vị trí
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    if (address != null && address.length() > 0) {
                        addressFull += address;
                    }
                    if (city != null && city != "null" && city.length() > 0) {
                        if (addressFull != null && addressFull.length() > 0) {
                            addressFull += ", " + city;
                        } else {
                            addressFull += city;
                        }
                    }
                    if (state != null && state != "null" && state.length() > 0) {
                        if (addressFull != null && addressFull.length() > 0) {
                            addressFull += ", " + state;
                        } else {
                            addressFull += state;
                        }
                    }
                    if (country != null && country != "null" && state.length() > 0) {
                        if (addressFull != null && addressFull.length() > 0) {
                            addressFull += ", " + country;
                        } else {
                            addressFull += country;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    addressFull = getString(R.string.unknown_location);
                }

                Intent intent = new Intent();
                intent.putExtra(UrlPattern.LAT_LOCATION_KEY, latLo);
                intent.putExtra(UrlPattern.LONG_LOCATION_KEY, longLo);
                intent.putExtra(LocationModel.Contents.ADDRESS, addressFull);
                Log.d(TAG, "send broadcast");
                intent.setAction("ACTION_UI");
                sendBroadcast(intent);
                LocationModel.LocationHelper.insertLocation(this, ChildModel.QueryHelper.getChildIdActive(this), latLo, longLo, addressFull);
            } else if (REQUEST_UPDATE_RULE_PARENT.equals(type_request)) {
                Log.d(TAG, "DOWNLOAD_RULE_PARENT");
                if(NetworkUtils.isNetworkConnected(mContext)){
                    TransferService.startActionDownload(mContext, TransferService.DOWNLOAD_RULE_PARENT);
                }else{
                    new PreferencesController(mContext).putRequestDownload(true);
                }
            }
        }


        if (/* Check if data needs to be processed by long running job */ true) {
            // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.

        } else {
            // Handle message within 10 seconds

        }

    }


    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }


}
