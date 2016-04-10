package com.example.andersson.musicapp;

import android.util.Log;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Andersson on 05/04/16.
 */

public abstract class InstrumentThread extends Thread implements Observer {

    private int loopTime = 100;
    private int bars = 10;
    public int i;
    public ArrayList<Integer> tempSoundList;
    public ArrayList<Integer> soundList;
    private final Object stopper = new Object();
    public TimeThread timer;
    public InstrumentActivity activity;
    public boolean set;

    public abstract void instrument(int index);
    public abstract void initiate();

    public InstrumentThread(InstrumentActivity activity,TimeThread timer){

        this.timer = timer;
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

                        instrument(i);

                    }

                }.start();

                i++;

                if (i == bars) {

                    i = 0;
                    break;

                }else{

                    try {

                        this.sleep((loopTime / bars) * 1000);

                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
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

                            instrument(i);

                        }
                    }.start();

                    i++;

                    if (i == bars) {

                        i = 0;
                        break;

                    } else {

                        try {

                            this.sleep((loopTime / bars) * 1000);

                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }
                }

            }
        }.start();
    }
}
