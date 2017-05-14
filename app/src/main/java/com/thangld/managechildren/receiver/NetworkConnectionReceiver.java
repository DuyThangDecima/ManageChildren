package com.thangld.managechildren.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.storage.controller.PreferencesController;

public class NetworkConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            Log.d("WifiReceiver", "Have Wifi Connection");
            String privilege = new PreferencesController(context).getPrivilege();
            if (privilege.equals(PreferencesController.PRIVILEGE_CHILD)) {
                // Nếu là trẻ con, thực hiện upload.
                TransferService.startActionUpload(context, TransferService.UPLOAD_ALL);
            } else if (privilege.equals(PreferencesController.PRIVILEGE_CHILD)) {
                // Nếu là bố mẹ, kiểm tra download.
            }
        } else {
            Log.d("WifiReceiver", "Don't have Wifi Connection");
        }


    }
}
