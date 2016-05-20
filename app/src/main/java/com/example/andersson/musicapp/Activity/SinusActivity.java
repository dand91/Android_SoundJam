package com.example.andersson.musicapp.Activity;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.view.View;
        import android.view.animation.Animation;
        import android.view.animation.RotateAnimation;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.example.andersson.musicapp.Instrument.SinusThread;
        import com.example.andersson.musicapp.R;
        import com.example.andersson.musicapp.SharedResources.SinusThreadHolder;

public class SinusActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private float sensorValue;
    private float prevSensorValue;
    private int rotations;
    private float calculatedValue;

    private SinusThread sinusThread;
    private SinusObservable sinusbservable;

    private TextView freqDisp;
    private ImageView freqKnobScale;
    private Button muteButton;



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

        muteButton = (Button) findViewById(R.id.buttonMute);

        muteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //audioTrack.setVolume(0.5f);
                    }
                }
        );


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
