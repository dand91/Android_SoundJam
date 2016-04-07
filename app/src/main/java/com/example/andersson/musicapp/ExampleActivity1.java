package com.example.andersson.musicapp;


import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ExampleActivity1 extends InstrumentActivity implements SensorEventListener {

    // GUI code

    private Button recordButton;
    private Button loopTimeButton;
    private EditText loopTimeText;
    private Button barButton;
    private EditText barText;

    //

    //  Sensor code

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView mAccelData;
    private int CurrentVal = 0;


    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float[] Reading = event.values;
        CurrentVal = (int) Reading[0] + (int)  Reading[1] + (int)  Reading[2] + 200;
        mAccelData.setText(String.valueOf(Math.abs(CurrentVal)));

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //

    // Instrument code

    public ExampleActivity1(){
        super();
    }

    @Override
    void generateSoundInfo(int index) { // should be connected to a sensor, it's called at new beatTime

        // android AIP typ drum machine API

        int min = 200;
        int max = 500;

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        // Listan att fylla i


        //soundList.add(randomNum);
        soundList.add(CurrentVal);
    }

    @Override
    String setName() { // Set the name, mostly for thread separation

        return "ExampleActivity1";
    }

    @Override
    int getActivity(){

        return R.layout.activity_example;
    }

    @Override
    int getMenu(){

        return R.menu.menu_example;
    }

    @Override
    InstrumentThread getInstrumentClass() {// Return corresponding instrument that the activity should use
        return new ExampleInstrumentThread(this,holder.getTimer());
    }

    @Override
    void initiate() { // Sets basic information regarding bars, looptime and possibly initial sound.

        instrument.setSoundList(new ArrayList<Integer>(Arrays.asList(200, 210, 220, 230, 240, 250, 260, 270)));
        instrument.setBars(8);
        instrument.setLoopTime(16);

        // Sensor initiate
        setContentView(R.layout.activity_example);
        this.mSensorManager =(SensorManager)getSystemService(SENSOR_SERVICE);
        this.mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mAccelData = (TextView)findViewById(R.id.dataView);
        //

        // GUI initiate

        loopTimeText = (EditText) findViewById(R.id.LoopTimeView);
        loopTimeButton = (Button) findViewById(R.id.loopTimeButton);
        loopTimeButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                try {

                    instrument.setLoopTime(Integer.valueOf(loopTimeText.getText().toString()));
                    loopTimeText.setText("");

                } catch (Exception e) {

                    if (instrument != null) {
                        loopTimeText.setText(instrument.getLoopTime());
                    } else {
                        loopTimeText.setText("");
                    }
                }
            }
        });

        barText = (EditText) findViewById(R.id.BarView);
        barButton = (Button) findViewById(R.id.BarButton);
        barButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                try {
                    instrument.setBars(Integer.valueOf(barText.getText().toString()));
                    barText.setText("");
                } catch (Exception e) {

                    if (instrument != null) {
                        barText.setText(instrument.getBars());
                    } else {
                        barText.setText("");
                    }
                }
            }

        });

        recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                index = 0;
                bars = instrument.getBars();
                loopTime = instrument.getLoopTime();
                barButton.setBackgroundColor(Color.RED);
                loopTimeButton.setBackgroundColor(Color.RED);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        barButton.setEnabled(true);
                        loopTimeButton.setEnabled(true);

                        barButton.setBackgroundColor(Color.GREEN);
                        loopTimeButton.setBackgroundColor(Color.GREEN);
                    }
                }, loopTime*1000);

                barButton.setEnabled(false);
                loopTimeButton.setEnabled(false);

                soundList = new ArrayList<Integer>();


                new Thread() {

                    public synchronized void run() {


                        while (true) {

                            try {

                                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(50);

                                index++;

                                generateSoundInfo(index);

                                if (index == bars) {

                                    break;
                                }

                                Thread.sleep(((loopTime / bars) * 1000) - 50);

                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
                        }

                        String s = "";
                        for(int in : soundList){
                            s = s + in + " ";
                        }
                        Log.d("EA - Record", s);

                        instrument.setSoundList(soundList);

                    }

                }.start();

            }
        });
        //

    }

}