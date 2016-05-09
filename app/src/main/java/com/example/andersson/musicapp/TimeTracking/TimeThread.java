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

            DecimalFormat df = new DecimalFormat("#.");

            while (true) {

                calendar = Calendar.getInstance();
                double second = calendar.get(Calendar.SECOND);
                double millisecond = calendar.get(Calendar.MILLISECOND);

                double time = Double.valueOf(df.format(second * 1000 + millisecond));
                loopTime = Double.valueOf(df.format(((MainActivity) holder.getMainActivity()).getLoopTime()*1000.0));

                if (time % (loopTime) == 0 && run) {

                    Log.i("TimerThread", "Run: " + time);

                    run = false;
                    ob.setChange();

                } else if (time % loopTime != 0) {

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
