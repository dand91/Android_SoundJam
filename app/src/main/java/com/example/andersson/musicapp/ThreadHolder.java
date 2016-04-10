package com.example.andersson.musicapp;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;

public class ThreadHolder implements Parcelable {

    private HashMap<String,Thread> threads;
    private TimeThread timer;
    private UpdateThread updater;
    private String groupName;
    private SoundPool mySound;
    private Activity mainActivity;

    private static ThreadHolder holder;

    public ThreadHolder(Activity mainActivity){

        this.mainActivity = mainActivity;
        mySound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        this.threads = new  HashMap<String,Thread>();
        this.timer = new TimeThread();
        this.timer.start();
        this.updater = new UpdateThread(this);

        Log.d("ThreadHolder","Initiating Timer and Updater");

    }

    public ThreadHolder(ThreadHolder holder) {

        this.holder = holder;

    }
    public boolean hasHolder(){

        return (holder != null)? true:false;
    }

    public TimeThread getTimer(){
        return timer;
    }
    public String getGroupName(){
        return groupName;
    }
    public void setGroupName(String groupName){
        this.groupName = groupName;
        if(!updater.isAlive()) {
            this.updater.start();
        }

    }
    public UpdateThread getUpdater(){return updater;}
    public SoundPool getSoundPool(){
        return mySound;
    }
    public Activity getMainActivity(){
        return mainActivity;
    }

    public void transfer(){

        if(holder != null){

            this.threads = holder.getThreads();
            this.timer = holder.getTimer();
            this.updater = holder.getUpdater();
            this.mySound = holder.getSoundPool();
            this.groupName = holder.getGroupName();

            Log.d("ThreadHolder","Transfering getThreads: " + threads.size());

        }else{

            Log.d("ThreadHolder", "No holders object");

        }
    }

    public HashMap<String,Thread> getThreads(){
        return threads;}
    public boolean containsKey(String key){

        Log.d("ThreadHolder","Contains: " + threads.containsKey(key));
        return threads.containsKey(key)? true:false;

    }
    public Thread getThread(String key){
        return threads.get(key);
    }
    public void addThread(String s,Thread t){

        if(timer != null) {

            timer.add((InstrumentThread) t);
            Log.d("ThreadHolder", "Timer is not null");

        }else{

            Log.d("ThreadHolder","Timer is null");
        }

        threads.put(s,t);

        Log.d("Thread", "Number of objects: " + threads.size());

    }

    protected ThreadHolder(Parcel in) {
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
    public static final Parcelable.Creator<ThreadHolder> CREATOR = new Parcelable.Creator<ThreadHolder>() {
        @Override
        public ThreadHolder createFromParcel(Parcel in) {
            return new ThreadHolder(in);
        }

        @Override
        public ThreadHolder[] newArray(int size) {
            return new ThreadHolder[size];
        }
    };
}