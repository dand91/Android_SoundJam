package com.example.andersson.musicapp.Activity;

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
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.andersson.musicapp.Instrument.ExampleInstrumentThread1;
import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.R;

import java.util.ArrayList;

public class ExampleInstrumentActivity1 extends AbstractInstrumentActivity implements SensorEventListener {

    // GUI/Instrument variables
    private Button recordButton;
    private Button loopTimeButton;
    private Button playButton;
    private Button stopButton;
    private EditText loopTimeText;
    private Button barButton;
    private EditText barText;
    // end GUI/Instrument variables

    //  Sensor variables
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView mAccelData;
    private int CurrentVal = 0;
    private boolean playRealTime;
    // end Sensor variables

    // Sensor code
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

        if(playRealTime) {

            float[] Reading = event.values;

            int X = (int) Reading[0];
            int Y = (int) Reading[1];
            int Z = (int) Reading[2];

            mAccelData.setText(String.valueOf("x: " + X + " y: " + Y + " z: " + Z));

            int tol = 1;

            if(X > tol && Y > tol){

                instrument.playRealTime(0);

            }else if(X > tol && Y < tol){

                instrument.playRealTime(1);

            }else if(X < tol && Y > tol){

                instrument.playRealTime(2);

            }else if(X < tol && Y < tol){

                instrument.playRealTime(3);

            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    // end Sensor Code

    // GUI/Instrument code
    public ExampleInstrumentActivity1(){
        super();
    }

    @Override
    void generateSoundInfo(int index) { // should be connected to a sensor, it's called at new beatTime

        //soundList.add(CurrentVal);
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
    AbstractInstrumentThread getInstrumentClass() {// Return corresponding playLoop that the activity should use
        return new ExampleInstrumentThread1(this,holder);
    }
    // end GUI/Instrument code

    @Override
    void initiate() { // Sets basic information regarding bars, looptime and possibly initial sound.


        // Sensor initiate
        setContentView(R.layout.activity_example);
        this.mSensorManager =(SensorManager)getSystemService(SENSOR_SERVICE);
        this.mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mAccelData = (TextView)findViewById(R.id.dataView);
        // end Sensor initiate

        // GUI/Instrument initiate
        playRealTime = false;
        int bar = 8;
        int loop = 8;

        //playLoop.setSoundList(new ArrayList<Integer>(Arrays.asList(1,0, 1, 0, 1, 0, 1, 0)));
        instrument.setBars(bar);
        instrument.setLoopTime(loop);

        loopGUI();
        barGUI();
        recordGUI();
        stopPlayGUI();
        volumeGUI();
        // end GUI/Instrument initiate

    }

    private void volumeGUI(){

            volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
            volumeSeekBar.setProgress(instrument.getVolume());
            volumeSeekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                instrument.setVolume(((float)i)/100);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        }

    private void stopPlayGUI(){

        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                playRealTime = true;
                Log.d("EA1","playRealTime");
            }
        });
        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                playRealTime = false;
                Log.d("EA1","stopRealTime");
            }
        });
    }

    private void loopGUI(){

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
    }
    private void barGUI(){

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
    }
    private void recordGUI(){

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
    }

}