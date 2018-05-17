package com.example.mzm.activity_inspector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        context.startService(new Intent(context, main_service.class));


    }
}
