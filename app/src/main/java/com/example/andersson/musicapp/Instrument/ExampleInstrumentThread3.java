package com.example.andersson.musicapp.Instrument;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

/**
 * Created by Andersson on 05/04/16.
 */

public class ExampleInstrumentThread3 extends AbstractInstrumentThread {

    long startTime = 0;
    long sampleTime = 500;

    private  int duration = 1; // seconds
    private  int sampleRate = 8000;
    private  int numSamples = duration * sampleRate;
    private  double sample[] = new double[numSamples];
    private  double freqOfTone = 440; // hz
    Handler handler = new Handler();

    private final byte generatedSnd[] = new byte[2 * numSamples];

    public ExampleInstrumentThread3(AbstractInstrumentActivity activity, SharedInfoHolder holder) {
        super(activity, holder);
    }

    @Override
    public void playLoop(int index) {


    }

    public void playRealTime(int value) {


        if (System.currentTimeMillis() - startTime > sampleTime) {

            Log.d("TEST","" + value);

            freqOfTone = value;
            final Thread thread = new Thread(new Runnable() {
                public void run() {
                    genTone();
                    handler.post(new Runnable() {

                        public void run() {
                            playSound();
                        }
                    });
                }
            });
            thread.start();
            startTime = System.currentTimeMillis();
        }


    }

    public void initiateSound() {


    }

    public void setVolume(float volume) {

    }

    public int getVolume() {

        return 1;
    }

    void genTone(){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    void playSound(){
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }


}
