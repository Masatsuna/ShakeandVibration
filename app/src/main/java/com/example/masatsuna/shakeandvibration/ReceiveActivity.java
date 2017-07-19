package com.example.masatsuna.shakeandvibration;

import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static java.lang.Thread.sleep;

public class ReceiveActivity extends AppCompatActivity {

    boolean flag = true;
    Thread thread;
    DatagramSocket sock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        int port = 50001;
        try {
            sock = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (flag) {
                    try {

                        byte buf[] = new byte[512];
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        sock.receive(packet);
                        String data = new String(packet.getData()).trim();
                        Log.d("tag", data);
                        if(data.equals("shake")) {
                            vibrator.vibrate(500);
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

                //handler.sendEmptyMessage(0);
            }

        });

        thread.start();
    }

//    Handler handler = new Handler(){
//        public void handleMessage(Message msg) {
//            finish();
//        }
//    };

    @Override
    protected void onPause(){
        super.onPause();
        //while (thread.isAlive());

    }

    public void onClick(View view) throws InterruptedException {
        flag = false;
        thread.interrupt();
        sock.close();
//        thread.join();
//        sleep(3000);
        finish();
    }
}
