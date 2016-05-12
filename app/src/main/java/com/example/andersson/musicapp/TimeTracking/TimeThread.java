package com.example.andersson.musicapp.TimeTracking;

import android.util.Log;

import com.example.andersson.musicapp.Activity.MainActivity;
import com.example.andersson.musicapp.Pool.ThreadPool;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;
import com.example.andersson.musicapp.SharedResources.TimeObservable;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observer;

/**
 * Created by Andersson on 07/04/16.
 */
public class TimeThread extends Thread {

    private static TimeThread instance = null;
    private TimeObservable ob;
    private ThreadHolder holder;
    private ThreadPool threadPool;
    private long adjust = 0;

    private TimeThread() {

        this.ob = new TimeObservable();

        threadPool = ThreadPool.getInstance();

        Thread tempThread = new Thread() {

            @Override
            public void run() {

                while (true) {

                    long returnTime = getTime();

                    if(returnTime > 0) {

                        adjust = returnTime - System.currentTimeMillis();

                    }

                    Log.d("TimeThread", "Adjusting: " + adjust);


                    try {

                        sleep(60000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            private long getTime() {

                String[] TIME_SERVER = {"0.se.pool.ntp.org","1.se.pool.ntp.org","2.se.pool.ntp.org","3.se.pool.ntp.org"};

                for (int i = 0; i < TIME_SERVER.length; i++) {

                    try {

                        NTPUDPClient timeClient = new NTPUDPClient();
                        Log.d("TimeThread", "Starting NTP version: " + timeClient.getVersion() + " Source: " + TIME_SERVER[i]);

                        timeClient.setDefaultTimeout(2000);

                        InetAddress inetAddress = InetAddress.getByName(TIME_SERVER[i]);
                        TimeInfo timeInfo = timeClient.getTime(inetAddress);

                        return timeInfo.getMessage().getTransmitTimeStamp().getTime();

                    } catch (UnknownHostException e2) {
                        Log.e("TimeThread", "URL error");
                    } catch (java.io.IOException e) {
                        Log.e("TimeThread", "Connection error");
                    }

                }

                return 0l;
            }
        };

        threadPool.add(tempThread, "getTime");
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

                int tempLoopTime = (int) (((MainActivity) holder.getMainActivity()).getLoopTime() * 1000);

                long currentTime = System.currentTimeMillis() + adjust;
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
