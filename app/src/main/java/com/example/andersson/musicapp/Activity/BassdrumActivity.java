package com.example.andersson.musicapp.Activity;

import android.content.Context;
import android.content.Intent;
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

import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.Instrument.BassdrumThread;
import com.example.andersson.musicapp.R;

import java.util.ArrayList;

public class BassdrumActivity extends AbstractInstrumentActivity implements SensorEventListener {


    // end GUI/Instrument variables
    // GUI/Instrument variables
    public Button recordButton;
    public Button playButton;
    public Button stopButton;
    public Button barButton;
    public EditText barText;
    public SeekBar volumeSeekBar;
    public TextView progressText;
    //  Sensor variables
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView mAccelData;

    private int countDown;
    private boolean isActive;
    // end Sensor variables

    private ShakeDetector mShakeDetector;

    // Sensor code
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mShakeDetector);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    // end Sensor Code

    // GUI/Instrument code
    public BassdrumActivity() {
        super();
    }

    @Override
    void generateSoundInfo(int index) { // should be connected to a sensor, it's called at new beatTime

        if(isActive) {
            soundList.add(1);
        }else{
            soundList.add(0);
        }

    }

    @Override
    public String getName() { // Set the name, mostly for thread separation

        return "BassdrumActivity";
    }

    @Override
    int getActivity() {

        return R.layout.activity_bassdrum;
    }

    @Override
    int getMenu() {

        return R.menu.menu_bassdrum;
    }

    @Override
    AbstractInstrumentThread getInstrumentClass() {// Return corresponding playLoop that the activity should use
        return new BassdrumThread(this, holder);
    }

    // end GUI/Instrument code

    @Override
    void initiate() { // Sets basic information regarding bars, looptime and possibly initial sound.


        // Sensor initiateSound
        this.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mAccelData = (TextView) findViewById(R.id.dataView);
        // end Sensor initiateSound

        playRealTime = false;
        bars = 8;
        loopTime = ((MainActivity)holder.getMainActivity()).getLoopTime();
        instrument.setLoopTime(loopTime);
        instrument.setBars(bars);


        //Insert shake code

        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {

                if (record) {

                    instrument.playRealTime(1);

                    new Thread() {

                        public void run() {

                            isActive = true;

                            try {

                                //sleep((long) ((loopTime / bars) / 2));
                                sleep(400);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            isActive = false;


                        }
                    }.start();

                } else if (playRealTime) {

                    instrument.playRealTime(1);

                }

            }
        });


    }

    @Override
    void initiateGUI() {

        // GUI/Instrument initiateSound

        recordGUI();
        barGUI();
        stopPlayGUI();
        volumeGUI();
        // end GUI/Instrument initiateSound

    }


    private void recordGUI() {

        recordButton = (Button) findViewById(R.id.recordButton);
        progressText = (TextView) findViewById(R.id.progressText);

        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                if (!record) {

                    index = 0;
                    instrument.setChangedStatus(true);
                    barButton.setBackgroundColor(Color.RED);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            barButton.setEnabled(true);
                            barButton.setBackgroundColor(Color.GREEN);

                        }
                    }, (long) (loopTime * 1000));

                    barButton.setEnabled(false);

                    soundList = new ArrayList<Integer>();


                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            // change UI elements here
                        }
                    });

                    new Thread() {

                        public void run() {

                            record = true;
                            instrument.setRecord(true);

                            try {

                                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(2*1000);

                                for(int i = 3; i >= 0 ; i--) {

                                    countDown = i;
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {

                                            progressText.setText("Record start in: " + countDown);

                                        }
                                    });

                                    sleep(1000);

                                }


                            while (true) {

                                    generateSoundInfo(index);


                                    index++;

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        progressText.setText("Beat " + index + " of " + bars);

                                    }
                                });

                                sleep(200);
                                v.vibrate(50);

                                    if (index == bars) {

                                        break;
                                    }

                                sleep((long) (((double) loopTime / (double) bars) * 1000) - 200);


                            }

                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }

                            String s = "";
                            for (int in : soundList) {
                                s = s + in + " ";
                         }
                            Log.i("Recorded: ", s);

                            instrument.setSoundList(soundList);
                            record = false;
                            instrument.setRecord(false);

                        }

                    }.start();
                }
            }
        });


    }

    private void volumeGUI() {

        volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setProgress(instrument.getVolume());
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                instrument.setVolume(((float) i) / 100);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

    }


    private void stopPlayGUI() {

        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                playRealTime = true;
                Log.d("EA1", "playRealTime");
            }
        });
        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                playRealTime = false;
                Log.d("EA1", "stopRealTime");
            }
        });
    }

    private void barGUI() {

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
                        barText.setText((int) instrument.getBars());
                    } else {
                        barText.setText("");
                    }
                }
            }

        });
    }

}