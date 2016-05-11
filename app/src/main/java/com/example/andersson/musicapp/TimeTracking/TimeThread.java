package com.example.andersson.musicapp.TimeTracking;

import android.util.Log;

import com.example.andersson.musicapp.Activity.MainActivity;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;
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
            boolean set = true;
            int adjust = 0;
            int tempLoopTime = 0;
            int oldLoopTime = 0;

            while (true) {


                calendar = Calendar.getInstance();
                int second = calendar.get(Calendar.SECOND) + 1;
                int millisecond = calendar.get(Calendar.MILLISECOND) + 1;
                int time = (int) (Math.round((second * 1000 + millisecond) / 10.0) * 10);
                tempLoopTime = (int) (Math.round((((MainActivity) holder.getMainActivity()).getLoopTime() * 1000) / 10.0) * 10);

                //int time = (int) (second * 1000 + millisecond);
                //int tempLoopTime = (int)(((MainActivity) holder.getMainActivity()).getLoopTime() * 1000);

                if(calendar.get(Calendar.SECOND) == 0 && set){

                    if(oldLoopTime != tempLoopTime){
                        adjust = 0;
                    }

                    int temp1 = (int)(60000/tempLoopTime);
                    //Log.e("TEST1", "" + temp1);
                    int temp2 = (temp1 + 1)*tempLoopTime - 60000 ;
                    //Log.e("TEST2", "" + temp2);
                    int temp3 = tempLoopTime - temp2;
                    //Log.e("TEST3", "" + temp3);
                    adjust = adjust + temp3;
                    //Log.e("TEST4", "" + adjust);

                    set = false;

                }else if(calendar.get(Calendar.SECOND)  != 0){

                    set = true;
                }

                if ((time + adjust) % tempLoopTime == 0 && run) {

                    Log.i("TimerThread", "Run: " + time);
                    oldLoopTime = tempLoopTime;
                    run = false;

                    ob.setChange();

                } else if ((time + adjust ) % tempLoopTime != 0) {

                    run = true;

                }
            }
        }
    }

    public void add(Observer newOb) {

        ob.addObserver(newOb);
    }

}
