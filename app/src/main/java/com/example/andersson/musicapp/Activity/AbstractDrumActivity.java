package com.example.andersson.musicapp.Activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.R;

public abstract class AbstractDrumActivity extends AbstractInstrumentActivity implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private boolean isActive;

    private ShakeDetector mShakeDetector;

    public AbstractDrumActivity() {
        super();
    }

    public abstract String getName();

    public abstract AbstractInstrumentThread getInstrumentClass();

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

    @Override
    protected void generateSoundInfo(int index) { // should be connected to a sensor, it's called at new beatTime

        if (isActive) {
            soundList.add(1);
            soundListText.setText(soundListText.getText() + " 1 ");
            holder.setBeatArray(this.getName(),index,true);
        } else {
            soundList.add(0);
            soundListText.setText(soundListText.getText() + " 0 ");
            holder.setBeatArray(this.getName(),index,false);
        }
    }

    @Override
    protected int getLayout() {

        return R.layout.activity_drum;
    }

    @Override
    protected int getMenu() {

        return R.menu.menu_drum;
    }

    // end GUI/Instrument code

    @Override
    protected void initiate() {

        this.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        playRealTime = false;
        bars = 8;
        loopTime = ((MainActivity) holder.getMainActivity()).getLoopTime();
        instrument.setLoopTime(loopTime);
        instrument.setBars(bars);

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
    protected void initiateGUI() {

        soundListGUI();
        recordGUI();
        barGUI();
        stopPlayGUI();
        volumeGUI();
        removeGUI();

    }

}