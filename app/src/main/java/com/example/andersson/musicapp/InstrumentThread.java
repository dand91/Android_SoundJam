package com.example.andersson.musicapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Andersson on 05/04/16.
 */
public abstract class InstrumentThread extends Thread implements Observer {

    private int loopTime = 100;
    private int bars = 10;
    public int i;
    public ArrayList<Integer> soundList;
    private final Object stopper = new Object();
    public TimeThread timer;
    public InstrumentActivity activity;

    public abstract void instrument(int index);
    public abstract void initiate();

    public InstrumentThread(InstrumentActivity activity,TimeThread timer){

        this.timer = timer;
        this.activity = activity;
        this.i = 1;

        soundList = new ArrayList<Integer>(Arrays.asList(200, 210, 220, 230, 240, 250, 260, 270));

        if(activity == null){

            Log.d("IT", "Constructor activity is null");

        }

        initiate();
    }

    public void run(){

        while(true) {

            Log.d("InstrumentThread","Stop");

            try {
                halt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d("InstrumentThread","Run");

            while (true) {

                instrument(i);

                i++;

                if (i == bars) {

                    i = 1;
                    break;
                }
                
                try {

                    this.sleep((loopTime / bars) * 1000);

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    public void setLoopTime(int loopTime){

        this.loopTime = loopTime;
        timer.setLoopTime(loopTime);

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

    public void setSoundList(ArrayList<Integer> soundList){

        this.soundList = soundList;
    }

    public void go() {
        synchronized (stopper) {
            stopper.notify();
        }
    }

    public void halt() throws InterruptedException {
        synchronized (stopper) {
            stopper.wait();
        }
    }

    @Override
    public void update(Observable o, Object arg){
        go();
    }

}
