package com.example.andersson.musicapp.TimeTracking;

import android.util.Log;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Andersson on 13/05/16.
 */
public class NTPThread extends Thread {

    private static long adjust = 0;

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
                Log.e("TimeThread", e2.getMessage());
            } catch (java.io.IOException e) {
                Log.e("TimeThread", "Connection error");
                Log.e("TimeThread", e.getMessage());

            }
        }

        return 0l;
    }

    public synchronized long getAdjust(){

        return adjust;
    }
}
