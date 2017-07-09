package com.example.masatsuna.shakeandvibration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by bluem on 2017/07/09.
 */

public class MyBroadcastReceiver extends BroadcastReceiver  {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        String message = bundle.getString("message");

        if(message.equals("vibration")) {
            Vibrator  vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }

    }


}
