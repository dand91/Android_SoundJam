package com.example.andersson.musicapp.SharedResources;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.andersson.musicapp.Activity.MainActivity;
import com.example.andersson.musicapp.AsyncUpdate.UpdateThread;
import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.TimeTracking.TimeThread;

import java.util.HashMap;
import java.util.Map;

public class SharedInfoHolder{

    private static SharedInfoHolder instance = null;

    private HashMap<String, Thread> threads;
    private TimeThread timer;
    private UpdateThread updater;
    private Activity mainActivity;

    public SharedInfoHolder() {

        this.threads = new HashMap<String, Thread>();
        this.timer = TimeThread.getInstance();
        this.updater = UpdateThread.getInstance();

    }

    public static SharedInfoHolder getInstance() {

        if (instance == null) {
            instance = new SharedInfoHolder();
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


    public void setLoopTime(int loopTime) {

        timer.setLoopTime(loopTime);

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

    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public Activity getMainActivity() {
        return mainActivity;
    }
}