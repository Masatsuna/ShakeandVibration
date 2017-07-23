package com.example.masatsuna.shakeandvibration;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Sensor sensor;
    SensorManager sensorManager;
    String mode = "vibe";
    boolean flag = true;
    float y, z, before_y = 0, before_z = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton:
                        mode = "vibe";
                        break;
                    case R.id.radioButton2:
                        mode = "bell";
                        break;
                }
            }

        });
    }


    /**
     * ブロードキャストアドレスを取得するメソッド
     * @return  broadcastAddress.toString().substring(1);
     */
    private static final String getBroadcastAddress() {
        try {
            for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements(); ) {
                NetworkInterface ni = niEnum.nextElement();
                if (!ni.isLoopback()) {
                    for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                        if (interfaceAddress != null) {
                            InetAddress broadcastAddress = interfaceAddress.getBroadcast();
                            if (broadcastAddress != null) {
                                return broadcastAddress.toString().substring(1);
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {

        }
        return null;
    }



    protected void onResume() {
        super.onResume();
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("tag", String.valueOf(event.values[0]));
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER || !flag) {
            return;
        }

        y = event.values[1];
        z = event.values[2];

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if ((before_y - y) > 10 && (before_z - z) < 15 ) {
                        try {
                            InetAddress ia = InetAddress.getByName(getBroadcastAddress());
                            int port = 50001;
                            String data = mode;
                            DatagramSocket sock = new DatagramSocket();
                            DatagramPacket packet = new DatagramPacket(
                                    data.getBytes(),
                                    data.getBytes().length,
                                    ia,
                                    port);
                            sock.send(packet);
                            flag = false;
                            sleep(1000);
                            flag = true;
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                    before_y = y;
                    before_z = z;
                }
            }).start();
        }


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

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
