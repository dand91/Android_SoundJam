package com.example.andersson.musicapp.Instrument;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.audiofx.EnvironmentalReverb;
import android.os.Build;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.Activity.SinusActivity;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Andersson on 05/04/16.
 */

public class SinusThread extends Thread implements Observer {

    private SinusActivity activity;

    private int amplitude = 50000;

    private AudioTrack audioTrack;
    private static final int SAMPLE_RATE = 44100;
    private static final int BASE_FREQUENCY = 0;
    private float frequency;
    private float calculatedValue = 0;
    boolean isRunning = true;


    public SinusThread(SinusActivity activity){

        this.activity = activity;

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {

        setPriority(Thread.MAX_PRIORITY);

        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize,
                AudioTrack.MODE_STREAM);

        EnvironmentalReverb reverb = new EnvironmentalReverb(1, 1);
        reverb.setDecayHFRatio((short) 1000);
        reverb.setDecayTime(10000);
        reverb.setDensity((short) 1000);
        reverb.setDiffusion((short) 1000);
        reverb.setReverbLevel((short) 100000);
        reverb.setReverbDelay(10000);
        reverb.setEnabled(true);

        audioTrack.attachAuxEffect(reverb.getId());
        audioTrack.setAuxEffectSendLevel(audioTrack.getMaxVolume());
        audioTrack.setVolume(0.05f);

        //audioTrack.attachAuxEffect(PresetReverb.getId);
        //audioTrack.setAuxEffectSendLevel(audioTrack.getMaxVolume());
        //audioTrack.attachAuxEffect(EnvironmentalReverb.PARAM_DECAY_TIME);

        audioTrack.play();

        short samples[] = new short[bufferSize];
        double ph = 0.0;

        while (isRunning) {


            frequency = BASE_FREQUENCY + calculatedValue;

            activity.setFreqText("" + round(frequency, 1));


            for (int i = 0; i < bufferSize; i++) {
                samples[i] = (short) (amplitude * Math.sin(ph));
                ph += 2 * Math.PI * frequency / SAMPLE_RATE;
            }
            audioTrack.write(samples, 0, bufferSize);
        }
        audioTrack.stop();
        audioTrack.release();
    }


    public static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }


    @Override
    public void update(Observable observable, Object o) {

        calculatedValue = (float) o;

    }

    public void addActivity(SinusActivity activity){

        this.activity = activity;
    }
}
