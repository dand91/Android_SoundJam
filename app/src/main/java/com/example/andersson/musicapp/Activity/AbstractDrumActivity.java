package com.example.andersson.musicapp.Activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.Pool.ThreadPool;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.BeatHolder;
import com.example.andersson.musicapp.SharedResources.MainHolder;

import java.util.ArrayList;

public abstract class AbstractDrumActivity extends AbstractInstrumentActivity implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private boolean isActive;
    private BeatHolder beatHolder;
    private MainHolder mainHolder;

    private ShakeDetector mShakeDetector;

    public AbstractDrumActivity() {

        super();

        beatHolder = BeatHolder.getInstance();
        mainHolder = MainHolder.getInstance();

    }

    public abstract String getName();

    public abstract AbstractInstrumentThread getInstrumentClass();

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mShakeDetector);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void generateSoundInfo(ArrayList<Integer> updateList, int index) { // should be connected to a sensor, it's called at new beatTime

        if (isActive) {

            updateList.add(1);
            soundListText.setText(soundListText.getText() + " 1 ");
            beatHolder.setBeatArray(this.getName(), index, true);

        } else {

            updateList.add(0);
            soundListText.setText(soundListText.getText() + " 0 ");
            beatHolder.setBeatArray(this.getName(), index, false);

        }
    }

    @Override
    protected int getLayout() {

        return R.layout.activity_drum;
    }

    @Override
    protected void initiate() {

        this.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        playRealTime = false;
        instrument.setPlayRealTime(false);

        double bars = instrument.getBars();
        instrument.setBars(bars);

        double loopTime = ((MainActivity) mainHolder.getMainActivity()).getLoopTime();
        instrument.setLoopTime(loopTime);

        final ThreadPool threadPool = ThreadPool.getInstance();

        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {

                if (record) {

                    instrument.playRealTime(1);

                    Thread tempThread = new Thread() {

                        public void run() {

                            isActive = true;

                            try {

                                sleep(400);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            isActive = false;

                        }
                    };

                    threadPool.add(tempThread, "Active");

                } else if (playRealTime) {

                    instrument.playRealTime(1);

                }
            }
        });
    }

    @Override
    protected void initiateGUI() {

        speedGUI();
        soundListGUI();
        recordGUI();
        stopPlayGUI();
        volumeGUI();
        removeGUI();
    }

}