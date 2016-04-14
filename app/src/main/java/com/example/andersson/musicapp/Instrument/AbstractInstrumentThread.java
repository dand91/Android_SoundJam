package com.example.andersson.musicapp.Instrument;

import android.util.Log;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Andersson on 05/04/16.
 */

public abstract class AbstractInstrumentThread extends Thread implements Observer {

    private double loopTime = 10;
    private double bars = 10;
    public int i;
    public ArrayList<Integer> tempSoundList;
    public ArrayList<Integer> soundList;
    private final Object stopper = new Object();
    public SharedInfoHolder holder;
    public AbstractInstrumentActivity activity;
    public boolean set;
    public float volume = 0.5f;

    public abstract void playLoop(int index);
    public abstract void playRealTime(int value);
    public abstract void initiate();
    public abstract void setVolume(float volume);
    public abstract int getVolume();

    public AbstractInstrumentThread(AbstractInstrumentActivity activity, SharedInfoHolder holder){

        this.holder = holder;
        this.activity = activity;
        this.i = 0;

        if(activity == null){

            Log.d("InstrumentThread", "Constructor activity is null");

        }

        initiate();
    }

    public void run(){}
    public void run1(){

        while(true) {

            Log.d("InstrumentThread","Stop");

            try {
                halt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d("InstrumentThread","Run");

            while (true) {

                new Thread(){
                    @Override
                    public void run() {

                        playLoop(i);

                    }

                }.start();

                i++;

                if (i == bars) {

                    i = 0;
                    break;

                }else{

                    try {

                        this.sleep((long)(loopTime / bars) * 1000);

                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setLoopTime(double loopTime){

        this.loopTime = loopTime;
        holder.getTimer().setLoopTime(loopTime);

    }
    public void setBars(double bars){

        this.bars = bars;

    }

    public double getLoopTime(){

        return loopTime;

    }
    public double getBars(){

        return bars;

    }

    public void setSoundList(ArrayList<Integer> soundList){

        set = true;
        this.soundList = soundList;
    }
    public ArrayList<Integer>  getSoundList(){
        if(set) {
            return soundList;
        }else{
            return new ArrayList<Integer>();
        }
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
    public void update(Observable o, Object arg) {

       tempSoundList = soundList;

        new Thread() {
            @Override
            public void run() {

                while (true) {

                    new Thread() {
                        @Override
                        public void run() {

                            playLoop(i);

                        }
                    }.start();

                    i++;

                    if (i == bars) {

                        i = 0;
                        break;

                    } else {

                        try {

                            this.sleep((long)((loopTime / bars) * 1000));
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }
                }

            }
        }.start();
    }
}
