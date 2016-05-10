package com.example.andersson.musicapp.Activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.Instrument.BassThread;
import com.example.andersson.musicapp.R;

import java.util.ArrayList;

public class BassActivity extends AbstractInstrumentActivity implements SensorEventListener {

    int X;
    int Y;
    int Z;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView mAccelData;

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

            mAccelData.setText("x:" + X + " y: " + Y + " z: " + Z);

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

    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }

    @Override
    void generateSoundInfo(ArrayList<Integer> list , int index) {

        int tol = 0;

        if (X > tol && Y > tol && Z > 0) {

            instrument.playRealTime(0);
            list.add(0);
            soundListText.setText(soundListText.getText() + " 0 ");

        } else if (X > tol && Y < tol && Z > 0) {

            instrument.playRealTime(1);
            list.add(1);
            soundListText.setText(soundListText.getText() + " 1 ");

        } else if (X < tol && Y > tol && Z > 0) {

            instrument.playRealTime(2);
            list.add(2);
            soundListText.setText(soundListText.getText() + " 2 ");

        } else if (X < tol && Y < tol && Z > 0) {

            instrument.playRealTime(3);
            list.add(3);
            soundListText.setText(soundListText.getText() + " 3 ");

        } else if (X > tol && Y > tol && Z < 0) {

            instrument.playRealTime(4);
            list.add(4);
            soundListText.setText(soundListText.getText() + " 4 ");

        } else if (X > tol && Y < tol && Z < 0) {

            instrument.playRealTime(5);
            list.add(5);
            soundListText.setText(soundListText.getText() + " 5 ");

        } else if (X < tol && Y > tol && Z < 0) {

            instrument.playRealTime(6);
            list.add(6);
            soundListText.setText(soundListText.getText() + " 6 ");

        } else if (X < tol && Y < tol && Z < 0) {

            instrument.playRealTime(8);
            list.add(7);
            soundListText.setText(soundListText.getText() + " 7 ");

        } else {

            instrument.playRealTime(4);
            list.add(8);
            soundListText.setText(soundListText.getText() + " 8 ");

        }
    }

    @Override
    public String getName() {

        return "BassActivity";
    }

    @Override
    protected int getLayout() {

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

        this.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mAccelData = (TextView) findViewById(R.id.dataView);

        playRealTime = false;
        double bars = instrument.getBars();
        double loopTime = ((MainActivity) holder.getMainActivity()).getLoopTime();
        instrument.setLoopTime(loopTime);
        instrument.setBars(bars);

    }

    @Override
    protected void initiateGUI() {

        soundListGUI();
        recordGUI();
        barGUI();
        stopPlayGUI();
        volumeGUI();
        removeGUI();

    }
}