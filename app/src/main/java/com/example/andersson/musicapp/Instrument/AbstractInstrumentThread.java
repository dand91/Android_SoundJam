package com.example.andersson.musicapp.Instrument;

import android.util.Log;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;
import com.example.andersson.musicapp.SharedResources.SoundPoolHolder;
import com.example.andersson.musicapp.SharedResources.TimeObservable;
import com.example.andersson.musicapp.SharedResources.UpdateObservable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Andersson on 05/04/16.
 */

public abstract class AbstractInstrumentThread extends Thread implements Observer {

    public int i;
    public ArrayList<Integer> soundList = new ArrayList<Integer>();
    public SharedInfoHolder holder;
    public AbstractInstrumentActivity activity;
    public boolean set;
    public boolean changed;
    public float volume = 0.5f;
    public boolean playRealTime;
    public boolean record;
    private double loopTime;
    private double bars;
    protected int soundId;
    protected SoundPoolHolder sph;

    public AbstractInstrumentThread(AbstractInstrumentActivity activity, SharedInfoHolder holder) {

        this.sph = SoundPoolHolder.getInstance();
        this.holder = holder;
        this.activity = activity;
        this.i = 0;

        if (activity == null) {

            Log.e("InstrumentThread", "Constructor activity is null");
            System.exit(0);

        } else {


            if (activity != null) {

                if (activity.getBaseContext() != null) {

                    if (sph.getSoundPool() != null) {

                        initiateSound();

                    } else {

                        Log.e("EIT2", "soundPool is null");
                        System.exit(0);

                    }

                } else {

                    Log.e("EIT2", "Context is null");
                    System.exit(0);

                }

            } else {

                Log.e("EIT2", "Activity is null");
                System.exit(0);

            }
        }
    }

    public abstract void playLoop(int index);

    public abstract void playRealTime(int value);

    protected abstract void initiateSound();

    public void run() {

    }

    public double getLoopTime() {

        return loopTime;

    }

    public void setLoopTime(double loopTime) {

        this.loopTime = loopTime;
        ((AbstractInstrumentActivity) activity).setLoopTime((int) loopTime);

    }

    public double getBars() {

        return bars;

    }

    public void setBars(double bars) {

        this.bars = bars;
        ((AbstractInstrumentActivity) activity).setBars((int) bars);

    }

    public ArrayList<Integer> getSoundList() {

        if (set) {

            return soundList;

        } else {

            return new ArrayList<Integer>();
        }
    }

    public void setSoundList(ArrayList<Integer> soundList) {

        set = true;
        setChangedStatus(true);
        this.soundList = soundList;


    }

    public int getVolume() {

        return (int) (volume * 100);

    }

    public void setVolume(float volume) {

        this.volume = volume;
    }

    public void setRecord(boolean record) {

        this.record = record;
    }
    public void setPlayRealTime(boolean playRealTime) {

        this.playRealTime = playRealTime;
    }


    public void setChangedStatus(boolean changed) {

        this.changed = changed;
    }

    public boolean getChangeStatus() {

        return changed;
    }

    @Override
    public void update(Observable o, Object arg) {

        if (o instanceof TimeObservable) {

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
                                sleep((long) ((loopTime / bars) * 1000));
                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
                        }
                    }

                }
            }.start();

        } else if (o instanceof UpdateObservable) {

            HashMap<String, Object> map = (HashMap<String, Object>) arg;

            if (((String) map.get("instrumentName")).equals(activity.getName())) {

                setVolume((float) map.get("volume"));
                ArrayList<Integer> tempList = (ArrayList<Integer>) map.get("soundList");
                setSoundList(tempList);
                activity.setSoundList(tempList);

            }

        }
    }

}
