package com.example.masatsuna.shakeandvibration;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Vibrator vibrator;
    Sensor sensor;
    SensorManager sensorManager;
    float before_y = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
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
        /*
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float y = event.values[1];
            if(before_y > 0 && (before_y - y) > 3){
                vibrator.vibrate(500);
            }
            before_y = y;
        }
        */
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onClick(View view) {
        Intent intent = new Intent(this, RecieveActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause () {
        super.onPause();
        // センサーへのイベントリスナーの解除
        sensorManager.unregisterListener(this);
    }
}
