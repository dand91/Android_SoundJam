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

import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.Instrument.SinusThread;
import com.example.andersson.musicapp.R;

import java.util.ArrayList;

public class SinusActivity extends AbstractInstrumentActivity implements SensorEventListener {

    // GUI/Instrument variables
    private Button recordButton;
    private Button playButton;
    private Button stopButton;
    private Button barButton;
    private EditText barText;
    private SeekBar volumeSeekBar;
    // end GUI/Instrument variables

    //  Sensor variables
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView mAccelData;
    private int CurrentVal = 0;

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

        if (playRealTime) {

            float[] Reading = event.values;

            int X = (int) ((int) 400 + Reading[0]);

            instrument.playRealTime(X);

        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    // end Sensor Code

    // GUI/Instrument code
    public SinusActivity() {
        super();
    }

    @Override
    void generateSoundInfo(int index) { // should be connected to a sensor, it's called at new beatTime

        //soundList.add(CurrentVal);
    }

    @Override
    public String getName() { // Set the name, mostly for thread separation

        return "SinusActivity";
    }

    @Override
    int getActivity() {

        return R.layout.activity_sinus;
    }

    @Override
    int getMenu() {

        return R.menu.menu_sinus;
    }

    @Override
    AbstractInstrumentThread getInstrumentClass() {// Return corresponding playLoop that the activity should use
        return new SinusThread(this, holder);
    }

    // end GUI/Instrument code

    @Override
    void initiate() { // Sets basic information regarding bars, looptime and possibly initial sound.


        // Sensor initiateSound
        setContentView(R.layout.activity_bassdrum);
        this.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mAccelData = (TextView) findViewById(R.id.dataView);
        // end Sensor initiateSound

        playRealTime = false;
        double bar = 16;

        //playLoop.setSoundList(new ArrayList<Integer>(Arrays.asList(1,0, 1, 0, 1, 0, 1, 0)));
        instrument.setBars(bar);

    }

    @Override
    void initiateGUI() {

        // GUI/Instrument initiateSound
        barGUI();
        recordGUI();
        stopPlayGUI();
        volumeGUI();
        // end GUI/Instrument initiateSound

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

    private void recordGUI() {

        recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                index = 0;
                bars = 16;
                loopTime = ((MainActivity)holder.getMainActivity()).getLoopTime();
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

                                Thread.sleep((long) ((loopTime / bars) * 1000) - 50);

                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
                        }

                        String s = "";
                        for (int in : soundList) {
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