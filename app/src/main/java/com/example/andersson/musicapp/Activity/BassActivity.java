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
import com.example.andersson.musicapp.Instrument.BassThread;
import com.example.andersson.musicapp.R;

import java.util.ArrayList;

public class BassActivity extends AbstractInstrumentActivity implements SensorEventListener {

    int X;
    int Y;
    int Z;
    // GUI/Instrument variables
    private Button recordButton;
    private Button playButton;
    private Button stopButton;
    // end GUI/Instrument variables
    private Button barButton;
    private EditText barText;
    private SeekBar volumeSeekBar;
    // end Sensor variables
    //  Sensor variables
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView mAccelData;

    // GUI/Instrument code
    public BassActivity() {
        super();
    }

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

            X = (int) Reading[0];
            Y = (int) Reading[1];
            Z = (int) (Reading[2]);

            mAccelData.setText(String.valueOf("x: " + X + " y: " + Y + " z: " + Z));

            int tol = 0;

            if (X > tol && Y > tol && Z > tol) {

                instrument.playRealTime(0);

            } else if (X > tol && Y < tol && Z > tol) {

                instrument.playRealTime(1);

            } else if (X < tol && Y > tol && Z > tol) {

                instrument.playRealTime(2);

            } else if (X < tol && Y < tol && Z > tol) {

                instrument.playRealTime(3);

            } else if (X > tol && Y > tol && Z < tol) {

                instrument.playRealTime(4);

            } else if (X > tol && Y < tol && Z < tol) {

                instrument.playRealTime(5);

            } else if (X < tol && Y > tol && Z < tol) {

                instrument.playRealTime(6);

            } else if (X < tol && Y < tol && Z < tol) {

                instrument.playRealTime(7);

            }

        } else if (record) {

            float[] Reading = event.values;

            X = (int) Reading[0];
            Y = (int) Reading[1];
            Z = (int) (Reading[2]);

        }

        mAccelData.setText("x:" + X + " y: " + Y + " z: " + Z);
    }
    // end Sensor Code

    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }

    @Override
    void generateSoundInfo(int index) {

        int tol = 0;

        if (X > tol && Y > tol && Z > 0) {

            instrument.playRealTime(0);
            soundList.add(0);

        } else if (X > tol && Y < tol && Z > 0) {

            instrument.playRealTime(1);
            soundList.add(1);

        } else if (X < tol && Y > tol && Z > 0) {

            instrument.playRealTime(2);
            soundList.add(2);

        } else if (X < tol && Y < tol && Z > 0) {

            instrument.playRealTime(3);
            soundList.add(3);

        } else if (X > tol && Y > tol && Z < 0) {

            instrument.playRealTime(4);
            soundList.add(4);

        } else if (X > tol && Y < tol && Z < 0) {

            instrument.playRealTime(5);
            soundList.add(5);

        } else if (X < tol && Y > tol && Z < 0) {

            instrument.playRealTime(6);
            soundList.add(6);

        } else if (X < tol && Y < tol && Z < 0) {

            instrument.playRealTime(8);
            soundList.add(7);

        } else {

            instrument.playRealTime(4);
            soundList.add(8);
        }
    }

    @Override
    public String getName() { // Set the name, mostly for thread separation

        return "BassActivity";
    }

    @Override
    protected int getActivity() {

        return R.layout.activity_bass;
    }

    @Override
    protected int getMenu() {

        return R.menu.menu_bass;
    }

    @Override
    protected AbstractInstrumentThread getInstrumentClass() {// Return corresponding playLoop that the activity should use
        return new BassThread(this, holder);
    }

    // end GUI/Instrument code

    @Override
    protected void initiate() { // Sets basic information regarding bars, looptime and possibly initial sound.


        // Sensor initiateSound
        setContentView(R.layout.activity_drum);
        this.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mAccelData = (TextView) findViewById(R.id.dataView);
        // end Sensor initiateSound

        playRealTime = false;
        bars = 8;
        loopTime = ((MainActivity) holder.getMainActivity()).getLoopTime();
        instrument.setLoopTime(loopTime);
        instrument.setBars(bars);

    }

    @Override
    protected void initiateGUI() {

        recordGUI();
        barGUI();
        stopPlayGUI();
        volumeGUI();

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
                        instrument.setChangedStatus(true);
                        instrument.setRecord(true);

                        while (true) {

                            try {

                                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(50);

                                index++;

                                generateSoundInfo(index);

                                if (index == bars) {

                                    break;
                                }

                                Thread.sleep((long) (((double) loopTime / (double) bars) * 1000));

                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
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