package com.example.andersson.musicapp.AsyncUpdate;

import android.util.Log;

import com.example.andersson.musicapp.SharedResources.ObservableObject;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

import java.util.Observer;

/**
 * Created by Andersson on 07/04/16.
 */
public class UpdateThread extends Thread{

    private SharedInfoHolder holder;
    private ObservableObject ob;

    public UpdateThread(SharedInfoHolder holder){

        this.holder = holder;
        ob = new ObservableObject();

    }

    public void run(){

        AsyncTask mAsyncTask = new AsyncTask();
        mAsyncTask.execute();

        if(holder == null){

            Log.d("ThreadHolder","Holder is null");

        }else {

            mAsyncTask.addHolder(holder);

            while(true) {

                mAsyncTask.doInBackground();

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public void add(Observer newOb){

        ob.addObserver(newOb);

    }
    public void setHolder(SharedInfoHolder holder){

        this.holder = holder;
    }
}

