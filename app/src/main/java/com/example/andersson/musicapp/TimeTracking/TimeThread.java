package com.example.andersson.musicapp.TimeTracking;

import android.util.Log;

import com.example.andersson.musicapp.Activity.MainActivity;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;
import com.example.andersson.musicapp.SharedResources.TimeObservable;

import java.text.DecimalFormat;
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
    private double loopTime;
    private ThreadHolder holder;

    private TimeThread() {

        this.setPriority(Thread.MAX_PRIORITY);
        this.ob = new TimeObservable();
    }

    public static TimeThread getInstance() {

        if (instance == null) {
            instance = new TimeThread();
        }
        return instance;
    }

    public void run() {

        holder = ThreadHolder.getInstance();

        if (holder == null) {

            Log.e("ThreadHolder", "Holder is null");
            System.exit(0);

        } else {

            boolean run = true;


            while (true) {

                calendar = Calendar.getInstance();
                int second = calendar.get(Calendar.SECOND);
                int millisecond = calendar.get(Calendar.MILLISECOND);
                int time = (int)(Math.round( (second * 1000 + millisecond) / 10.0) * 10);
                int tempLoopTime = (int)(Math.round( (((MainActivity) holder.getMainActivity()).getLoopTime()*1000) / 10.0) * 10);


                if (time % tempLoopTime == 0 && run) {

                    Log.i("TimerThread", "Run: " + time);

                    run = false;

                    ob.setChange();

                }else if (time % tempLoopTime != 0) {

                   run = true;

                }
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
