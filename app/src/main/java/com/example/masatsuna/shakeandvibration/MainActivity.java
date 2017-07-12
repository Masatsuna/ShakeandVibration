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
                            InetAddress ia = InetAddress.getByName("172.17.252.176");
                            int port = 50001;
                            String data = "shake";
                            DatagramSocket sock = new DatagramSocket();
                            DatagramPacket packet = new DatagramPacket(
                                    data.getBytes(),
                                    data.getBytes().length,
                                    ia,
                                    port);
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
