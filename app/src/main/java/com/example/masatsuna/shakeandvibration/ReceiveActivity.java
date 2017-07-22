package com.example.masatsuna.shakeandvibration;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ReceiveActivity extends AppCompatActivity {

    boolean flag = true;
    Thread thread;
    DatagramSocket sock;
    SoundPool soundPool;
    int soundId;
    ImageView imageView;

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

        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageView = (ImageView) findViewById(R.id.imageView);
                switch (message.obj.toString()) {
                    case "vibe":
                        imageView.setImageResource(R.drawable.vibe);
                        break;
                    case "bell":
                        imageView.setImageResource(R.drawable.bell);
                        break;

                }



            }
        };



        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (flag) {
                    try {

                        byte buf[] = new byte[512];
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        sock.receive(packet);
                        String data = new String(packet.getData()).trim();
                        Message message = Message.obtain();
                        message.obj = new String(data);
                        handler.sendMessage(message);
                        switch (data) {
                            case "vibe":
                                vibrator.vibrate(500);
                                break;
                            case "bell":
                                soundPool.play(soundId, 1, 1, 0, 0, 1);
                                break;
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageResource(R.drawable.recieve);
                            }
                        }, 400);



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
