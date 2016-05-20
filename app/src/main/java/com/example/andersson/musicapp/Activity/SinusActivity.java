package com.example.andersson.musicapp.Activity;

        import android.annotation.TargetApi;
        import android.content.Intent;
        import android.database.Observable;
        import android.media.audiofx.EnvironmentalReverb;
        import android.os.Build;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.media.AudioFormat;
        import android.media.AudioManager;
        import android.media.AudioTrack;
        import android.util.Log;
        import android.view.animation.Animation;
        import android.view.animation.RotateAnimation;
        import android.widget.ImageView;
        import android.widget.SeekBar;
        import android.widget.TextView;
        import android.widget.Button;
        import android.widget.EditText;

        import com.example.andersson.musicapp.Instrument.SinusThread;
        import com.example.andersson.musicapp.Pool.ThreadPool;
        import com.example.andersson.musicapp.R;
        import com.example.andersson.musicapp.SharedResources.SinusThreadHolder;
        import com.example.andersson.musicapp.SharedResources.ThreadHolder;

public class SinusActivity extends AppCompatActivity implements SensorEventListener {


    private SensorManager mSensorManager;

    private float sensorValue;
    private float prevSensorValue;
    private int rotations;
    private float calculatedValue;

    private String info;

    private SinusThread sinusThread;
    private Sinusbservable sinusbservable;

    private TextView freqDisp;
    private ImageView freqKnobScale;

    private Thread audioGenerator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinus);

        SinusThreadHolder sinThreadHolder = SinusThreadHolder.getInstance();

        sinusbservable = new Sinusbservable();

        if (!sinThreadHolder.hasSinusTread()) {

            sinusThread = new SinusThread(this);
            sinThreadHolder.setSinusThread(sinusThread);

        } else {

            sinusThread = sinThreadHolder.getSinusThread();

        }

        sinusbservable.addObserver(sinusThread);


        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        freqDisp = (TextView) findViewById(R.id.textViewFreqency);
        freqKnobScale = (ImageView) findViewById(R.id.imageViewFreqKnobScale);

        prevSensorValue = 0;
        rotations = 0;

    }


    protected void onResume() {
        super.onResume();


        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
                , SensorManager.SENSOR_DELAY_GAME);
    }


    protected void onPause() {
        super.onPause();

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

        sinusbservable.Sinusnotify(calculatedValue);

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

    private class Sinusbservable extends java.util.Observable {

        public void Sinusnotify(float value) {

            notifyObservers(value);
            setChanged();
        }

    }
}
