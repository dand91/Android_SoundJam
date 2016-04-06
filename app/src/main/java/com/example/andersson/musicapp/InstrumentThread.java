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
    public ArrayList<Integer> freqOfTone;


    public abstract void instrument(int index);

    public InstrumentThread(){

        freqOfTone = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
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

}
