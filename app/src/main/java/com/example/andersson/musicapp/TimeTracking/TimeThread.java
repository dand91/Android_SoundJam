package com.example.andersson.musicapp.TimeTracking;

import android.util.Log;

import com.example.andersson.musicapp.SharedResources.TimeObservable;

import java.util.Calendar;
import java.util.Observer;

/**
 * Created by Andersson on 07/04/16.
 */
public class TimeThread extends Thread {

    private static TimeThread instance = null;
    int i;
    private Calendar calendar;
    private TimeObservable ob;
    private double loopTime = 8;

    private TimeThread() {

        ob = new TimeObservable();
    }

    public static TimeThread getInstance() {
        if (instance == null) {
            instance = new TimeThread();
        }
        return instance;
    }

    public void run() {

        boolean run = true;

        while (true) {

            calendar = Calendar.getInstance();
            int seconds = calendar.get(Calendar.SECOND);

            if (seconds % loopTime == 0 && run) {

                Log.i("TimerThread", "Run: " + seconds);

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
