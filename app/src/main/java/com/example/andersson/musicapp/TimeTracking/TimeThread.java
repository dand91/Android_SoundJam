package com.example.andersson.musicapp.TimeTracking;

import android.util.Log;

import com.example.andersson.musicapp.SharedResources.TimeObservable;

import java.util.Calendar;
import java.util.Observer;

/**
 * Created by Andersson on 07/04/16.
 */
public class TimeThread extends Thread {

    private Calendar calendar;
    private TimeObservable ob;
    private double loopTime = 8;
    int i;

    private static TimeThread instance = null;

    public static TimeThread getInstance() {
        if(instance == null) {
            instance = new TimeThread();
        }
        return instance;
    }

    private TimeThread() {

        ob = new TimeObservable();
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

            } else if (seconds % loopTime != 0) {

                run = true;

            }
        }
    }

    public void setLoopTime(double loopTime) {

        this.loopTime = loopTime;

    }

    public void add(Observer newOb) {

        ob.addObserver(newOb);
    }
}
