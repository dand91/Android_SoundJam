package com.example.andersson.musicapp.Instrument;

import android.util.Log;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.Activity.MainActivity;
import com.example.andersson.musicapp.AsyncUpdate.UpdateObservable;
import com.example.andersson.musicapp.AsyncUpdate.UpdateThread;
import com.example.andersson.musicapp.Pool.ThreadPool;
import com.example.andersson.musicapp.SharedResources.MainHolder;
import com.example.andersson.musicapp.SharedResources.SoundPoolHolder;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;
import com.example.andersson.musicapp.TimeTracking.TimeObservable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Andersson on 05/04/16.
 */

public abstract class AbstractInstrumentThread extends Thread implements Observer {

    public int i = 0;
    public ArrayList<Integer> soundList = new ArrayList<Integer>();
    public ThreadHolder threadHolder;
    public AbstractInstrumentActivity activity;
    public boolean set;
    public boolean changed;
    public float volume = 0.5f;
    public boolean playRealTime;
    public boolean record;
    public boolean pause;
    protected int soundId;
    protected SoundPoolHolder sph;
    protected double bars;
    private double loopTime;
    private double timeDifference = 0;

    public AbstractInstrumentThread(AbstractInstrumentActivity activity) {

        this.sph = SoundPoolHolder.getInstance();
        this.threadHolder = ThreadHolder.getInstance();
        this.activity = activity;

        if (activity == null) {

            Log.e("InstrumentThread", "Constructor activity is null");
            System.exit(0);

        } else {


            if (activity != null) {

                if (activity.getBaseContext() != null) {

                    if (sph.getSoundPool() != null) {

                        initiateSound();

                    } else {

                        Log.e("InstrumentThread", "soundPool is null");
                        System.exit(0);

                    }

                } else {

                    Log.e("InstrumentThread", "Context is null");
                    System.exit(0);

                }

            } else {

                Log.e("InstrumentThread", "Activity is null");
                System.exit(0);

            }
        }
    }

    public abstract void playLoop(int index);

    public abstract void playRealTime(int value);

    protected abstract void initiateSound();

    protected abstract void setBeat();

    public void run() {

    }

    public double getLoopTime() {

        return loopTime;

    }

    public void setLoopTime(double loopTime) {

        this.loopTime = loopTime;

    }

    public double getBars() {

        if (soundList.size() == 0) {

            return 8;

        } else {

            return bars;

        }

    }

    public void setBars(double bars) {

        this.bars = bars;

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
        this.soundList = soundList;
        UpdateThread.getInstance().wake();

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


    public void setPause(boolean pause) {

        this.pause = pause;
    }

    public void setChangedStatus(boolean changed) {

        this.changed = changed;
    }

    public boolean getChangeStatus() {

        return changed;
    }

    @Override
    public void update(Observable o, Object arg) {

        final ThreadPool threadPool = ThreadPool.getInstance();

        if (o instanceof TimeObservable) {

            loopTime = ((MainActivity) MainHolder.getInstance().getMainActivity()).getLoopTime();

            final double tempLoopTime = getLoopTime() * 1000;
            final double loopBars = getBars();

            if ((!soundList.isEmpty() && soundList.get(0) != Integer.MAX_VALUE) |
                    (playRealTime & this instanceof BassThread) |
                    (record & this instanceof BassThread)) {

                Thread playThread = new Thread() {

                    @Override
                    public void run() {

                        long startTime = System.currentTimeMillis();

                        for(int i = 0; i < loopBars; i++){

                            playLoop(i);

                                try {

                                   sleep( (long) ( (tempLoopTime / loopBars)
                                           + ( ( timeDifference) / loopBars) ) );

                                } catch (InterruptedException e) {

                                    e.printStackTrace();
                                }
                        }

                        double difference = (loopTime*1000 - (double)(System.currentTimeMillis() - startTime));
                        timeDifference = difference + timeDifference;

                        Log.e("TEST","Time: "  + (loopTime*1000 - (double)(System.currentTimeMillis() - startTime)));

                    }
                };

                threadPool.add(playThread, "play");

            }

        } else if (o instanceof UpdateObservable) {

            ((MainActivity) MainHolder.getInstance().getMainActivity()).setGroupNameButton(true);

            final HashMap<String, Object> map = (HashMap<String, Object>) arg;

            Runnable updateThread = () -> {

                if (((String) map.get("instrumentName")).equals(activity.getName())) {

                    float tempVolume = (float) map.get("volume");
                    double tempBar = Double.valueOf((int) map.get("bars"));
                    ArrayList<Integer> tempList = (ArrayList<Integer>) map.get("soundList");

                    Log.e("TEST2","Updating tempVolume: " + tempVolume);
                    Log.e("TEST2","Updating tempBar: " + tempVolume);
                    Log.e("TEST2","Updating tempList: " + tempList);

                    setVolume(tempVolume);

                    if(!record) {

                        setSoundList(tempList);
                        setBars(tempBar);

                        setBeat();

                    }
                }
            };

            threadPool.add(new Thread(updateThread), "update");

        }
    }

    public void resetTimeDifference(){

        timeDifference = 0;

    }
}
