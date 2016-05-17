package com.example.andersson.musicapp.SharedResources;

import android.util.Log;

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

    public ThreadHolder() {

        this.threads = new HashMap<String, Thread>();
        this.timer = TimeThread.getInstance();
        this.updater = UpdateThread.getInstance();

    }

    public static ThreadHolder getInstance() {

        if (instance == null) {
            instance = new ThreadHolder();
        }
        return instance;
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

        Log.d("ThreadHolder", "Number of objects: " + threads.size());

    }

}