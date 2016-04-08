package com.example.andersson.musicapp;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observer;

/**
 * Created by Andersson on 07/04/16.
 */
public class UpdateThread extends Thread{

    private ThreadHolder holder;
    private ObservableObject ob;

    public UpdateThread(ThreadHolder holder){

        this.holder = holder;
        ob = new ObservableObject();

    }

    public void run(){

        MyAsyncTask mMyAsyncTask = new MyAsyncTask();
        mMyAsyncTask.execute();

        while(true) {

            if(holder == null){

                Log.d("ThreadHolder","NULL");
            }
            mMyAsyncTask.addHolder(holder);
            mMyAsyncTask.doInBackground();

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(Observer newOb){

        ob.addObserver(newOb);
    }



}

