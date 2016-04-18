package com.example.andersson.musicapp.Instrument;

import android.util.Log;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;
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

    private double loopTime = 10;
    private double bars = 10;
    public int i;
    public ArrayList<Integer> soundList;
    public SharedInfoHolder holder;
    public AbstractInstrumentActivity activity;
    public boolean set;
    public float volume = 0.5f;

    public abstract void playLoop(int index);

    public abstract void playRealTime(int value);

    protected abstract void initiateSound();


    public AbstractInstrumentThread(AbstractInstrumentActivity activity, SharedInfoHolder holder) {

        this.holder = holder;
        this.activity = activity;
        this.i = 0;

        if (activity == null) {

            Log.d("InstrumentThread", "Constructor activity is null");
            System.exit(0);

        } else {


            if (activity != null) {

                if (activity.getContext() != null) {

                    if (holder.getSoundPool() != null) {

                        initiateSound();

                    } else {

                        Log.d("EIT2", "soundPool is null");
                        System.exit(0);

                    }

                } else {

                    Log.d("EIT2", "Context is null");
                    System.exit(0);

                }

            } else {

                Log.d("EIT2", "Activity is null");
                System.exit(0);

            }


        }
    }

    public void run() {

    }


    public void setLoopTime(double loopTime) {

        this.loopTime = loopTime;
        holder.getTimer().setLoopTime(loopTime);

    }

    public void setBars(double bars) {

        this.bars = bars;

    }

    public double getLoopTime() {

        return loopTime;

    }

    public double getBars() {

        return bars;

    }

    public void setSoundList(ArrayList<Integer> soundList) {

        set = true;
        this.soundList = soundList;

    }

    public ArrayList<Integer> getSoundList() {

        if (set) {

            return soundList;

        } else {

            return new ArrayList<Integer>();
        }
    }


    public void setVolume(float volume) {

        this.volume = volume;
    }

    public int getVolume() {

        return (int) (volume * 100);

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
                setSoundList((ArrayList<Integer>) map.get("soundList"));

            }

        }
    }

}
