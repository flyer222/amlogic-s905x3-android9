package com.android.tv.settings.display.dolbyvision;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.os.Process;

public class DvReceiver extends BroadcastReceiver {
    //static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive (Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, DolbyVisionService.class);
            context.startService(serviceIntent);
        }
    }
}
