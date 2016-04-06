package com.example.andersson.musicapp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by Andersson on 05/04/16.
 */
public abstract class InstrumentThread extends Thread {

    private int loopTime = 100;
    private int bars = 10;
    int i;
    private Calendar calendar;

    // Copy
    private final double duration = 1; // seconds
    private final int sampleRate = 8000;
    private final int numSamples = (int) duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private ArrayList<Integer> freqOfTone; // hz
    private final double freqOfTone2 = 440;
    private final byte generatedSnd[] = new byte[2 * numSamples];
    //

    public InstrumentThread(){

        freqOfTone = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));
        i = 1;

    }

    public void run(){


        while(true) {

            calendar = Calendar.getInstance();
            int seconds = calendar.get(Calendar.SECOND);

            if (seconds % loopTime == 0) {

                while (true) {

                    instrument(i);

                    i++;

                    if (i == bars) {

                        i = 1;
                        break;
                    }

                    try {

                        this.sleep((loopTime/bars)*1000);

                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void setLoopTime(int loopTime){

        this.loopTime = loopTime;

    }
    public void setBars(int bars){

        this.bars = bars;

    }

    public int getLoopTime(){

        return loopTime;

    }
    public int getBars(){

        return bars;

    }


    public void setSoundList(ArrayList<Integer> freqOfTone){

        this.freqOfTone = freqOfTone;
    }

    public abstract void instrument(int index);


    void genTone(int index){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone.get(index)));
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

    void playSound(){
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }

}
