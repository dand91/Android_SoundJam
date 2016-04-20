package com.example.andersson.musicapp.Activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SensorActivityTemp extends AppCompatActivity implements SensorEventListener {



    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private SoundPool mySound;
    private int soundId;
    private Button soundButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_sensor);

       // mySound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
      //  soundId = mySound.load(this, R.raw.bd, 1);

//    SensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);



    }


    protected void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume

        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        //mSensorManager.registerListener(mShakeDetector, mAccelerometer,	2);
    }

    protected void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        Log.d("x:", String.valueOf(x));
        Log.d("y:", String.valueOf(y));
        Log.d("z:", String.valueOf(z));


    }

    public void playSound() {
        mySound.play(soundId, 1, 1, 1, 0, 1);
    }





}
