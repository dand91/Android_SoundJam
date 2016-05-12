package com.example.andersson.musicapp.AsyncUpdate;

import android.util.Log;

import com.example.andersson.musicapp.SharedResources.ThreadHolder;
import com.example.andersson.musicapp.SharedResources.UpdateObservable;

import java.util.Observer;

/**
 * Created by Andersson on 07/04/16.
 */
public class UpdateThread extends Thread {

    private static final int timer = 5;
    private static UpdateThread instance = null;
    private ThreadHolder holder;
    private UpdateObservable ob;

    private UpdateThread() {

        this.ob = new UpdateObservable();

    }

    public static UpdateThread getInstance() {

        if (instance == null) {

            instance = new UpdateThread();
        }

        return instance;
    }

    public void run() {

        holder = ThreadHolder.getInstance();

        if (holder == null) {

            Log.e("UpdateThread", "Holder is null");
            System.exit(0);

        } else {

            AsyncTask mAsyncTask = new AsyncTask();
            mAsyncTask.execute();

            mAsyncTask.addHolder(holder);
            mAsyncTask.addObserver(ob);

            while (true) {

                Log.i("UpdateThread", " Running UpdateTask.");

                mAsyncTask.doInBackground();

                try {
                    Thread.sleep(timer * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void add(Observer newOb) {

        ob.addObserver(newOb);
    }
}

