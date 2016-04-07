package com.example.andersson.musicapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class ThreadHolder implements Parcelable {

    private HashMap<String,Thread> threads;
    private TimeThread timer;

    private static ThreadHolder holder;

    public ThreadHolder(){

        this.threads = new  HashMap<String,Thread>();
        this.timer = new TimeThread();
        this.timer.start();

    }

    public ThreadHolder(ThreadHolder holder) {

        this.holder = holder;
        Log.d("ThreadHolder","Added holders and time object");

    }
    public boolean hasHolder(){

        return (holder != null)? true:false;
    }

    public TimeThread getTimer(){
        return timer;
    }


    public void transfer(){

        if(holder == null){
            threads = new  HashMap<String,Thread>();
            Log.d("ThreadHolder", "No holders object");

        }else{

            threads = holder.threads();
            this.timer = holder.getTimer();
            Log.d("ThreadHolder","Transfering threads: " + threads.size());

        }

    }

    public HashMap<String,Thread> threads(){
        return threads;}
    public boolean containsKey(String key){

        Log.d("ThreadHolder","Contains: " + threads.containsKey(key));
        return threads.containsKey(key)? true:false;

    }
    public Thread get(String key){
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