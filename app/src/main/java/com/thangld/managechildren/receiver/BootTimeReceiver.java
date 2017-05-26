package com.thangld.managechildren.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.thangld.managechildren.collector.observer.ObserverService;

public class BootTimeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intentService = new Intent(context, ObserverService.class);
        intent.setAction(ObserverService.TYPE_BOOT_TIME);
        context.startService(intentService);
    }
}
