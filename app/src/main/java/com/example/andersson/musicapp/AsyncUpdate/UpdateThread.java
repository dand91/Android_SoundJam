package com.example.andersson.musicapp.AsyncUpdate;

import android.util.Log;

import com.example.andersson.musicapp.SharedResources.ThreadHolder;
import com.example.andersson.musicapp.SharedResources.UpdateObservable;

import java.util.Observer;

/**
 * Created by Andersson on 07/04/16.
 */

public class UpdateThread extends Thread {

    private static final int SERVER_UPDATE_TIME = 2000;

    private static UpdateThread instance = null;

    private ThreadHolder threadHolder;
    private UpdateObservable observable;

    private UpdateThread() {

        this.observable = new UpdateObservable();

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

            AsyncTask asyncTask = new AsyncTask();
            asyncTask.execute();

            asyncTask.addHolder(threadHolder);
            asyncTask.addObserver(observable);

            while (true) {

                Log.i("UpdateThread", " Running UpdateTask.");

                asyncTask.doInBackground();

                try {

                    Thread.sleep(SERVER_UPDATE_TIME);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void add(Observer newOb) {

        observable.addObserver(newOb);
    }
}

