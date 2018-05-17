package com.example.mzm.activity_inspector;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class MessageReciever extends ResultReceiver {

    private MainActivity.Message message;
    public MessageReciever(MainActivity.Message message) {
        super(new Handler());
        this.message = message;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        message.msgFromService(resultCode, resultData);
    }
}
