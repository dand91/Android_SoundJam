package com.example.andersson.musicapp.SharedResources;

import android.app.Activity;
import android.util.Log;

import com.example.andersson.musicapp.Activity.MainActivity;
import com.example.andersson.musicapp.AsyncUpdate.UpdateThread;
import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.TimeTracking.TimeThread;

import java.util.HashMap;
import java.util.Map;

public class ThreadHolder {

    private static ThreadHolder instance = null;

    private HashMap<String, Thread> threads;
    private TimeThread timer;
    private UpdateThread updater;
    private Activity mainActivity;
    private HashMap<String, Boolean> beat;

    public ThreadHolder() {

        this.threads = new HashMap<String, Thread>();
        this.timer = TimeThread.getInstance();
        this.updater = UpdateThread.getInstance();
        beat = new HashMap<String, Boolean>();

    }

    public static ThreadHolder getInstance() {

        if (instance == null) {
            instance = new ThreadHolder();
        }
        return instance;
    }

    public String getGroupName() {

        return ((MainActivity) mainActivity).getGroupName();
    }

    public HashMap<String, Thread> getThreads() {
        return threads;
    }

    public Thread getThread(String key) {
        return threads.get(key);
    }


    public void setLoopTime(double loopTime) {

        for (Map.Entry<String, Thread> thread : threads.entrySet()) {

            ((AbstractInstrumentThread) thread.getValue()).setLoopTime(loopTime);
        }
    }

    public boolean containsKey(String key) {

        return threads.containsKey(key) ? true : false;

    }

    public void addThread(String name, Thread thread) {

        if (timer != null) {

            timer.add((AbstractInstrumentThread) thread);

        } else {

            Log.e("ThreadHolder", "Timer is null");
            System.exit(0);
        }

        if (updater != null) {

            updater.add((AbstractInstrumentThread) thread);

        } else {

            Log.e("ThreadHolder", "Updater is null");
            System.exit(0);

        }

        threads.put(name, thread);

        Log.d("Thread", "Number of objects: " + threads.size());

    }

    public Activity getMainActivity() {

        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public HashMap<String, Boolean> getBeatMap() {

        return beat;
    }

    public void setBeatArray(String instrument, int index, boolean on) {

        if (!beat.containsKey(instrument + index)) {
            beat.put(instrument + index, on);
        } else {
            beat.remove(instrument + index);
            beat.put(instrument + index, on);
        }

    }

    public void clearBeatArray() {

        beat.clear();

    }
}