package com.example.andersson.musicapp.TimeTracking;

import android.util.Log;

import com.example.andersson.musicapp.Activity.MainActivity;
import com.example.andersson.musicapp.Pool.ThreadPool;
import com.example.andersson.musicapp.SharedResources.MainHolder;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;
import com.example.andersson.musicapp.SharedResources.TimeObservable;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

/**
 * Created by Andersson on 07/04/16.
 */
public class TimeThread extends Thread {

    private static TimeThread instance = null;
    private TimeObservable ob;
    private ThreadHolder holder;
    private MainHolder mainHolder;
    private ThreadPool threadPool;
    private long adjust = 0;

    private TimeThread() {

        this.ob = new TimeObservable();

        threadPool = ThreadPool.getInstance();
        mainHolder = MainHolder.getInstance();

        Thread tempThread = new NTPThread();

        threadPool.add(tempThread, "NTP");
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

            Log.e("TimeThread", "Holder is null");
            System.exit(0);

        } else {

            boolean run = true;

            while (true) {

                int tempLoopTime = (int) (((MainActivity) mainHolder.getMainActivity()).getLoopTime() * 1000);

                long currentTime = 0;

                try {

                    currentTime = System.currentTimeMillis() + ((NTPThread)threadPool.getThread("NTP")).getAdjust();

                } catch (Exception e) {
                    Log.e("TimeThread", "Error feching adjust from thread.");
                    Log.e("TimeThread", e.getMessage());

                }

                long tempTime = currentTime % tempLoopTime;

                if (tempTime == 0 && run) {

                    Log.i("TimeThread", "Run: " + currentTime);
                    run = false;
                    ob.setChange();

                } else if (tempTime != 0) {

                    run = true;

                }
            }
        }
    }

    public void add(Observer newOb) {

        ob.addObserver(newOb);
    }

}
