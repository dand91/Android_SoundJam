package com.example.andersson.musicapp.AsyncUpdate;

import android.util.Log;

import java.util.Observer;

/**
 * Created by Andersson on 07/04/16.
 */

public class UpdateThread extends Thread {

    private static final int SERVER_UPDATE_TIME = 2000;

    private static UpdateThread instance = null;

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

        AsyncTask asyncTask = new AsyncTask();
        asyncTask.execute();

        asyncTask.addObserver(observable);

        while (true) {

            Log.i("UpdateThread", " Running UpdateTask.");

            asyncTask.doInBackground();

            try {

                sleep(SERVER_UPDATE_TIME);

            } catch (InterruptedException e) {

                Log.i("UpdateThread", "Thread interrupted");
            }
        }
    }

    public void add(Observer newOb) {

        observable.addObserver(newOb);
    }

    public void wake() {

        interrupt();
    }
}

