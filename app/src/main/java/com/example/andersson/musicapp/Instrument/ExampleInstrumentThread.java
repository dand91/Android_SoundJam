package com.example.andersson.musicapp.Instrument;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

/**
 * Created by Andersson on 05/04/16.
 */

public class ExampleInstrumentThread extends AbstractInstrumentThread {

    private final double duration = 1; // seconds
    private final int sampleRate = 8000;
    private final int numSamples = (int) duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private final double freqOfTone2 = 440;
    private final byte generatedSnd[] = new byte[2 * numSamples];

    public ExampleInstrumentThread(AbstractInstrumentActivity activity, SharedInfoHolder holder) {
        super(activity, holder);
    }

    @Override
    public void playLoop(int index) {

        if (soundList.size() > 0) {

            String s = "";
            for (int in : soundList) {
                s = s + in + " ";
            }

            Log.d("EIT - instrumentCall", s);

            // This is were the sound generation takes place, index is the current bar
            this.genTone(index);
            this.playSound();
            Log.d("EIT - Play", "Playing");

        }
    }

    public void playRealTime(int value) {

    }

    void genTone(int index) {
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {

            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / soundList.get(index)));
            //sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone2));
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

    void playSound() {
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }

    public void initiateSound() {


    }

    public void setVolume(float volume) {

    }

    public int getVolume() {

        return 1;
    }

}
