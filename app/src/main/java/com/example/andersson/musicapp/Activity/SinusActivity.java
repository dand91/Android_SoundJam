package com.example.andersson.musicapp.Activity;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.util.Log;
        import android.view.View;
        import android.view.animation.Animation;
        import android.view.animation.RotateAnimation;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.SeekBar;
        import android.widget.TextView;

        import com.example.andersson.musicapp.Instrument.SinusThread;
        import com.example.andersson.musicapp.R;
        import com.example.andersson.musicapp.SharedResources.SinusThreadHolder;

public class SinusActivity extends BaseActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private float sensorValue;
    private float prevSensorValue;
    private int rotations;
    private float calculatedValue;
    private boolean mute;

    private SinusThread sinusThread;
    private SinusObservable sinusbservable;

    private SeekBar volumeSeekBar;
    private TextView freqDisp;
    private ImageView freqKnobScale;
    private Button MuteButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinus);

        SinusThreadHolder sinThreadHolder = SinusThreadHolder.getInstance();

        sinusbservable = new SinusObservable();

        if (!sinThreadHolder.hasSinusTread()) {

            sinusThread = new SinusThread(this);
            sinThreadHolder.setSinusThread(sinusThread);

        } else {

            sinusThread = sinThreadHolder.getSinusThread();

        }

        sinusThread.addActivity(this);
        sinusbservable.addObserver(sinusThread);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        freqDisp = (TextView) findViewById(R.id.textViewFreqency);
        freqKnobScale = (ImageView) findViewById(R.id.imageViewFreqKnobScale);

        prevSensorValue = 0;
        rotations = 0;

        volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setProgress((int)(sinusThread.getVolume()*1000));
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                sinusThread.setVolume(((float)i/1000));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }

        });

        MuteButton = (Button) findViewById(R.id.muteButton);
        MuteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mute = !mute;
                Log.e("SinusAtivity", "Mute: " + mute);
                sinusThread.setMute(mute);

            }
        });
    }


    public void onResume() {
        super.onResume();


        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
                , SensorManager.SENSOR_DELAY_GAME);
    }



    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    public void onSensorChanged(SensorEvent event) {

        sensorValue = event.values[0];

        if (prevSensorValue != 0) {
            if ((sensorValue - prevSensorValue) < -100) {
                rotations++;
            } else if ((sensorValue - prevSensorValue) > 100) {
                rotations--;
            }
        }

        calculatedValue = (rotations * 360) + sensorValue;

        sinusbservable.SinusNotify(calculatedValue);

        RotateAnimation ra = new RotateAnimation(-prevSensorValue, -sensorValue, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //RotateAnimation ra = new RotateAnimation(prevSensorValue, sensorValue);
        //ra.setDuration(210);
        //ra.setFillAfter(true);
        freqKnobScale.startAnimation(ra);

        prevSensorValue = sensorValue;

    }

/*    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        try {
            audioGenerator.join();
        } catch (InterruptedException e) {

            Log.e("SinusActivity","Thread join error");
        }
        audioGenerator = null;
    }*/


    public void setFreqText(String text) {

        runOnUiThread(() -> {
            freqDisp.setText(text);

        });
    }

    private class SinusObservable extends java.util.Observable {

        public void SinusNotify(float value) {

            notifyObservers(value);
            setChanged();
        }

    }
}
