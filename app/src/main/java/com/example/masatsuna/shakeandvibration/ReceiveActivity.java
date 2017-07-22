package com.example.masatsuna.shakeandvibration;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ReceiveActivity extends AppCompatActivity {

    boolean flag = true;
    Thread thread;
    DatagramSocket sock;
    SoundPool soundPool;
    int soundId;

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

                        switch (data) {
                            case "vibe":
                                vibrator.vibrate(500);
                                break;
                            case "bell":
                                soundPool.play(soundId, 1, 1, 0, 0, 1);
                                break;
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

            }

        });

        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(getApplicationContext(), R.raw.bell, 0);
    }

    public void onClick(View view) throws InterruptedException {
        flag = false;
        thread.interrupt();
        sock.close();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundPool.release();
    }
}
