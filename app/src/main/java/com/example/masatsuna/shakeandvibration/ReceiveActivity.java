package com.example.masatsuna.shakeandvibration;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ReceiveActivity extends AppCompatActivity {

    IntentFilter intentFilter;
    MyBroadcastReceiver myBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        myBroadcastReceiver = new MyBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("SHAKE");
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    public void onClick(View view) {
        finish();
    }
}
