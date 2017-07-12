package com.example.masatsuna.shakeandvibration;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Sensor sensor;
    SensorManager sensorManager;
    float before_y = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    protected void onResume() {
        super.onResume();
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float y = event.values[1];
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (before_y > 0 && (before_y - y) > 3) {
                        try {
                            // IPアドレスを取得（InetAddress型）
                            InetAddress ia = InetAddress.getByName("172.17.252.176");

                            // 送信ポート
                            int port = 50001;

                            // 送信データ （第３引数より）
                            String data = "shake";

                            // データグラムソケットを構築し、ローカルホストマシン上の使用可能なポートにバインド
                            DatagramSocket sock = new DatagramSocket();

                            // 送信パケット生成
                            // DatagramPacket(byte[] buf, int length, InetAddress address, int port) コンストラクタ
                            DatagramPacket packet = new DatagramPacket(
                                    data.getBytes(),                // String クラス getBytesメソッド利用
                                    data.getBytes().length,            // 配列の特徴 length利用
                                    ia,
                                    port);

                            // パケット送信
                            sock.send(packet);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                    before_y = y;
                }
            }).start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onClick(View view) {
        Intent intent = new Intent(this, ReceiveActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause () {
        super.onPause();
        // センサーへのイベントリスナーの解除
        sensorManager.unregisterListener(this);
    }
}
