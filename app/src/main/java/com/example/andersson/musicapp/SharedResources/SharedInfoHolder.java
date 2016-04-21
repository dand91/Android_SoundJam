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

public class SharedInfoHolder implements Parcelable {

    private HashMap<String, Thread> threads;
    private TimeThread timer;
    private UpdateThread updater;
    private SoundPool mySound;
    private Activity mainActivity;
    private static SharedInfoHolder holder;

    public SharedInfoHolder(Activity mainActivity) {

        this.mainActivity = mainActivity;
        this.mySound = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
        this.threads = new HashMap<String, Thread>();
        this.timer = TimeThread.getInstance();
        this.timer.start();
        this.updater = UpdateThread.getInstance();
        this.updater.setHolder(this);
        this.updater.start();

        Log.d("ThreadHolder", "Initiating Timer and Updater");

    }

    public SharedInfoHolder(SharedInfoHolder holder) {

        SharedInfoHolder.holder = holder;

    }

    public TimeThread getTimer() {
        return timer;
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

    public UpdateThread getUpdater() {
        return updater;
    }

    public SoundPool getSoundPool() {
        return mySound;
    }

    public void setLoopTime(int loopTime){

        timer.setLoopTime(loopTime);

        for(Map.Entry<String, Thread> thread : threads.entrySet()){

            ((AbstractInstrumentThread)thread.getValue()).setLoopTime(loopTime);

        }

    }

    public Activity getMainActivity() {

        return mainActivity;
    }

    public void transfer() {

        if (holder != null) {

            this.threads = holder.getThreads();
            this.timer = holder.getTimer();
            this.updater = holder.getUpdater();
            this.mySound = holder.getSoundPool();
            this.mainActivity = holder.getMainActivity();
            this.updater.setHolder(this);

            Log.d("ThreadHolder", "Transfering objects. getThreads: " + threads.size());

        } else {

            Log.d("ThreadHolder", "No holders object");
            System.exit(0);

        }
    }

    public boolean hasHolder() {

        return (holder != null) ? true : false;
    }

    public boolean containsKey(String key) {

        return threads.containsKey(key) ? true : false;

    }

    public void addThread(String name, Thread thread) {

        if (timer != null) {

            timer.add((AbstractInstrumentThread) thread);

        } else {

            Log.d("ThreadHolder", "Timer is null");
            System.exit(0);
        }

        if (updater != null) {

            updater.add((AbstractInstrumentThread) thread);

        } else {

            Log.d("ThreadHolder", "Updater is null");
            System.exit(0);

        }

        threads.put(name, thread);

        Log.d("Thread", "Number of objects: " + threads.size());

    }

    protected SharedInfoHolder(Parcel in) {

        threads = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(threads);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SharedInfoHolder> CREATOR = new Parcelable.Creator<SharedInfoHolder>() {
        @Override
        public SharedInfoHolder createFromParcel(Parcel in) {
            return new SharedInfoHolder(in);
        }

        @Override
        public SharedInfoHolder[] newArray(int size) {
            return new SharedInfoHolder[size];
        }
    };
}