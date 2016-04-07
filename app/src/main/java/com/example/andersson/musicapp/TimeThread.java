package com.example.andersson.musicapp;

import android.util.Log;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Andersson on 07/04/16.
 */
public class TimeThread extends Thread{

    private ObservableObject ob;
    private int loopTime = 10;
    int i;

    private Calendar calendar;

    public TimeThread(){

        ob = new ObservableObject();
    }
    public void run() {


        boolean run = true;


        while (true) {

            calendar = Calendar.getInstance();
            int seconds = calendar.get(Calendar.SECOND);

            if (seconds % loopTime == 0 && run) {

                Log.d("TimerThread", "Run: " + seconds);

                run = false;
                ob.setChange();

            }else if(seconds % loopTime != 0){


                run = true;

            }
        }
    }

    public void setLoopTime(int loopTime){

        this.loopTime = loopTime;

    }
    public void add(Observer newOb){

        ob.addObserver(newOb);
    }
}
