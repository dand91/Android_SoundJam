package com.example.andersson.musicapp;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private SoundPool mySound;
    private int soundId;

    private Button soundButton;

    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);


        mySound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId = mySound.load(this, R.raw.bd, 1);

        soundButton = (Button) findViewById(R.id.soundButton);
        soundButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Perform action on click
                playSound();
            }
        });

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }


    protected void onResume() {
        super.onResume();


        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                , SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    public void onSensorChanged(SensorEvent event) {
        playSound();
    }


    public void playSound() {
        mySound.play(soundId, 1, 1, 1, 0, 1);
    }

}