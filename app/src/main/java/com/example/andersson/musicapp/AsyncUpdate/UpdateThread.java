package com.example.andersson.musicapp.AsyncUpdate;

import android.util.Log;

import com.example.andersson.musicapp.SharedResources.ThreadHolder;
import com.example.andersson.musicapp.SharedResources.UpdateObservable;

import java.util.Observer;

/**
 * Created by Andersson on 07/04/16.
 */
public class UpdateThread extends Thread {

    private static final int UPDATE_TIME = 5000;
    private static UpdateThread instance = null;
    private ThreadHolder threadHolder;
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

        threadHolder = ThreadHolder.getInstance();

        if (threadHolder == null) {

            Log.e("UpdateThread", "Holder is null");
            System.exit(0);

        } else {

            AsyncTask mAsyncTask = new AsyncTask();
            mAsyncTask.execute();

            mAsyncTask.addHolder(threadHolder);
            mAsyncTask.addObserver(ob);

            while (true) {

                Log.i("UpdateThread", " Running UpdateTask.");

                mAsyncTask.doInBackground();

                try {
                    Thread.sleep(UPDATE_TIME);
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

