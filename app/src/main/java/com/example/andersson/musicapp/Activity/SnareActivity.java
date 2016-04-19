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
import com.example.andersson.musicapp.Instrument.SnareThread;
import com.example.andersson.musicapp.R;

import java.util.ArrayList;

public class SnareActivity extends AbstractInstrumentActivity implements SensorEventListener {

    // GUI code
    private Button recordButton;
    private Button barButton;
    private EditText barText;
    private SeekBar volumeSeekBar;
    // end GUI


    //  Sensor variables
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView mAccelData;
    private int CurrentVal = 0;

    private boolean isActive;
    // end Sensor variables


    // Instrument code
    public SnareActivity() {
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
    public void onSensorChanged(SensorEvent sensorEvent) {

        //Insert shake code

        isActive = true;
        try {
            Thread.sleep((long) ((loopTime/bars)/2));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isActive = false;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public String getName() { // Set the name, mostly for thread separation

        return "SnareActivity";
    }

    @Override
    int getActivity() {

        return R.layout.activity_snare;
    }

    @Override
    int getMenu() {

        return R.menu.menu_snare;
    }

    @Override
    AbstractInstrumentThread getInstrumentClass() {// Return corresponding playLoop that the activity should use
        return new SnareThread(this, holder);
    }

    @Override
    void initiate() { // Sets basic information regarding bars, looptime and possibly initial sound.

        bars = 16;
        loopTime = ((MainActivity)holder.getMainActivity()).getLoopTime();
        instrument.setLoopTime(loopTime);
        instrument.setBars(bars);

    }

    @Override
    void initiateGUI() {

        // GUI/Initiate initiateSound
        barGUI();
        recordGUI();
        volumeGUI();
        // end GUI/Instrumet initiateSound

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

                    public void run() {

                        record = true;

                        while (true) {

                            try {

                                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(50);

                                index++;

                                generateSoundInfo(index);

                                if (index == bars) {

                                    break;
                                }

                                Thread.sleep((long) (((double) loopTime / (double) bars) * 1000) - 50);

                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
                        }

                        String s = "";
                        for (int in : soundList) {
                            s = s + in + " ";
                        }
                        Log.d("Recorded: ", s);

                        instrument.setSoundList(soundList);
                        record = false;
                    }

                }.start();
            }
        });
    }


}