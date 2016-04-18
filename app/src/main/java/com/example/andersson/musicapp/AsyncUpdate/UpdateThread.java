package com.example.andersson.musicapp.AsyncUpdate;

import android.util.Log;

import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;
import com.example.andersson.musicapp.SharedResources.UpdateObservable;

import java.util.Observer;

/**
 * Created by Andersson on 07/04/16.
 */
public class UpdateThread extends Thread {

    private SharedInfoHolder holder;
    private UpdateObservable ob;
    private static final int timer = 10;

    public UpdateThread(SharedInfoHolder holder) {

        this.holder = holder;
        this.ob = new UpdateObservable();

    }

    public void run() {

        AsyncTask mAsyncTask = new AsyncTask();
        mAsyncTask.execute();

        if (holder == null) {

            Log.d("ThreadHolder", "Holder is null");

        } else {

            mAsyncTask.addHolder(holder);
            mAsyncTask.addObserver(ob);

            while (true) {

                Log.d("UpdateThread", " Running UpdateTask.");

                mAsyncTask.doInBackground();

                try {
                    Thread.sleep(timer * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setHolder(SharedInfoHolder holder) {

        this.holder = holder;
    }

    public void add(Observer newOb) {

        ob.addObserver(newOb);
    }

}

