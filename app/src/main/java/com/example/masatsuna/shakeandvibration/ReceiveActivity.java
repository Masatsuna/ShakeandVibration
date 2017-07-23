package com.example.masatsuna.shakeandvibration;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
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
    boolean flag2 = true;
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

        //画像を変化させるhandler
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


        /**
         * パケットを受信後、その内容に応じて処理する
         */
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (flag) {

                    if (flag2) {

                        flag2 = false;

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

                        flag2 = true;
                    }
                }
            }
        });
        thread.start();
    }




    @Override
    protected void onResume() {
        super.onResume();
        AudioAttributes audioAttributes;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(1)
                    .build();
        }
        else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

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
