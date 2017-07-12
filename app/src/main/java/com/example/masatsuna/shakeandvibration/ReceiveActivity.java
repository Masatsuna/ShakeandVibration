package com.example.masatsuna.shakeandvibration;

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ReceiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        int port = 50001;
                        DatagramSocket sock = new DatagramSocket(port);
                        byte buf[] = new byte[512];
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        sock.receive(packet);
                        vibrator.vibrate(500);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        }).start();
    }

    public void onClick(View view) {
        finish();
    }
}