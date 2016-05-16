package com.example.andersson.musicapp.TimeTracking;

import android.util.Log;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Andersson on 13/05/16.
 */
public class NTPThread extends Thread {

    private long adjust = 0;
    private long returnTime;
    private TimeInfo timeInfo;
    private NTPObservable observable;

    private ArrayList<Long> timeList;
    private static final int UPDATE_TIME = 60000;
    private static final int TIMEOUT = 2000;

    public NTPThread(){

        this.observable = new NTPObservable();
        this.timeList = new ArrayList<Long>();

    }

    @Override
    public void run() {

        while (true) {

            returnTime = getNTPUpdate();

            if (returnTime > 0) {

                adjust = returnTime - System.currentTimeMillis();
                timeList.add(adjust);
                Log.d("TimeThread", "Adjusting: " + adjust);

            }

            getInfo();

            try {

                sleep(UPDATE_TIME);

            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
    }

    private long getNTPUpdate() {

        String[] TIME_SERVER = {"0.se.pool.ntp.org", "1.se.pool.ntp.org", "2.se.pool.ntp.org", "3.se.pool.ntp.org"};

        for (int i = 0; i < TIME_SERVER.length; i++) {

            try {

                String server = TIME_SERVER[i];

                NTPUDPClient timeClient = new NTPUDPClient();

                Log.d("TimeThread", "Starting NTP version: " + timeClient.getVersion() + " Server: " + server);

                timeClient.setDefaultTimeout(TIMEOUT);

                InetAddress inetAddress = InetAddress.getByName(server);
                timeInfo = timeClient.getTime(inetAddress);

                return timeInfo.getMessage().getTransmitTimeStamp().getTime();

            } catch (UnknownHostException e1) {
                Log.e("TimeThread", "URL error");
                Log.e("TimeThread", "Message: " + e1.getMessage());
            } catch (java.io.IOException e2) {
                Log.e("TimeThread", "Connection error");
                Log.e("TimeThread", "Message: " + e2.getMessage());

            }
        }

        return 0l;
    }

    public synchronized long getAdjustment() {

        return adjust;
    }

    public synchronized long getTime(){

        return returnTime;
    }

    public synchronized double getMean(){

        double n = timeList.size();
        double sumTime = 0;

        for(Long time : timeList){

            sumTime += time;

        }

        return sumTime/n;
    }

    public synchronized double getVariance(){

        double n = timeList.size();
        double mean = getMean();
        double sumTime = 0;

        for(Long time : timeList){

            sumTime += Math.sqrt(time - mean);

        }

        return sumTime/n;
    }

    public void notifyObserversTime(){

        observable.notifyObservers(returnTime);
        observable.setChanged();
    }

    public void notifyObserversAdjustment(){

        observable.notifyObservers(adjust);
        observable.setChanged();

    }

    public void addObserver(Observer observer){

        observable.addObserver(observer);
    }

    public void getInfo() {

        try {

            timeInfo.computeDetails();

            Log.i("TimeThread", "TimeMean: " + getMean());
            Log.i("TimeThread", "TimeVariance: " + getVariance());
            Log.i("TimeThread", "ReturnTime: " + timeInfo.getReturnTime());
            Log.i("TimeThread", "DelayTime: " + timeInfo.getDelay());
            Log.i("TimeThread", "Offset: " + timeInfo.getOffset());

            Log.i("TimeThread", "Message:ModeName: " + timeInfo.getMessage().getModeName());
            Log.i("TimeThread", "Message:Mode: " + timeInfo.getMessage().getMode());
            Log.i("TimeThread", "Mode: Except in broadcast mode, an NTP association is formed when two peers\n" +
                    "exchange messages and one or both of them create and maintain an\n" +
                    "instantiation of the protocol machine, called an association. The\n" +
                    "association can operate in one of five modes as indicated by the host-\n" +
                    "mode variable (peer.mode): symmetric active, symmetric passive, client,\n" +
                    "server and broadcast, which are defined as follows:\n" +
                    "\n" +
                    "Symmetric Active (1): A host operating in this mode sends periodic\n" +
                    "messages regardless of the reachability state or stratum of its peer. By\n" +
                    "operating in this mode the host announces its willingness to synchronize\n" +
                    "and be synchronized by the peer.\n" +
                    "\n" +
                    "Symmetric Passive (2): This type of association is ordinarily created\n" +
                    "upon arrival of a message from a peer operating in the symmetric active\n" +
                    "mode and persists only as long as the peer is reachable and operating at\n" +
                    "a stratum level less than or equal to the host; otherwise, the\n" +
                    "association is dissolved. However, the association will always persist\n" +
                    "until at least one message has been sent in reply. By operating in this\n" +
                    "mode the host announces its willingness to synchronize and be\n" +
                    "synchronized by the peer.\n" +
                    "\n" +
                    "Client (3): A host operating in this mode sends periodic messages\n" +
                    "regardless of the reachability state or stratum of its peer. By\n" +
                    "operating in this mode the host, usually a LAN workstation, announces\n" +
                    "its willingness to be synchronized by, but not to synchronize the peer.\n" +
                    "\n" +
                    "Server (4): This type of association is ordinarily created upon arrival\n" +
                    "of a client request message and exists only in order to reply to that\n" +
                    "request, after which the association is dissolved. By operating in this\n" +
                    "mode the host, usually a LAN time server, announces its willingness to\n" +
                    "synchronize, but not to be synchronized by the peer.\n" +
                    "\n" +
                    "Broadcast (5): A host operating in this mode sends periodic messages\n" +
                    "regardless of the reachability state or stratum of the peers. By\n" +
                    "operating in this mode the host, usually a LAN time server operating on\n" +
                    "a high-speed broadcast medium, announces its willingness to synchronize\n" +
                    "all of the peers, but not to be synchronized by any of them.\n");

            Log.i("TimeThread", "Message:Precision: " + timeInfo.getMessage().getPrecision());
            Log.i("TimeThread", "Precision (sys.precision, peer.precision, pkt.precision): This is a\n" +
                    "signed integer indicating the precision of the various clocks, in\n" +
                    "seconds to the nearest power of two. The value must be rounded to the\n" +
                    "next larger power of two; for instance, a 50-Hz (20 ms) or 60-Hz (16.67\n" +
                    "ms) power-frequency clock would be assigned the value -5 (31.25 ms),\n" +
                    "while a 1000-Hz (1 ms) crystal-controlled clock would be assigned the\n" +
                    "value -9 (1.95 ms).");

            Log.i("TimeThread", "Message:Type: " + timeInfo.getMessage().getType());
            Log.i("TimeThread", "Message:ReferenceIdString: " + timeInfo.getMessage().getReferenceIdString());

            Log.i("TimeThread", "Message:RootDispersionInMillis: " + timeInfo.getMessage().getRootDispersionInMillis());
            Log.i("TimeThread","Root Dispersion (sys.rootdispersion, peer.rootdispersion,\n" +
                    "pkt.rootdispersion): This is a signed fixed-point number indicating the\n" +
                    "maximum error relative to the primary reference source at the root of\n" +
                    "the synchronization subnet, in seconds. Only positive values greater\n" +
                    "than zero are possible.");

            Log.i("TimeThread", "Message:Stratum: " + timeInfo.getMessage().getStratum());
            Log.i("TimeThread","Stratum (sys.stratum, peer.stratum, pkt.stratum): This is an integer\n" +
                    "indicating the stratum of the local clock, with values defined as\n" +
                    "follows:\n" +
                    "\n" +
                    "@Z_TBL_BEG = COLUMNS(2), DIMENSION(IN), COLWIDTHS(E1,E8), WIDTH(5.0000),\n" +
                    "ABOVE(.0830), BELOW(.0830), HGUTTER(.0560), KEEP(OFF), ALIGN(CT)\n" +
                    "\n" +
                    "@Z_TBL_BODY = TABLE TEXT, TABLE TEXT\n" +
                    "\n" +
                    "0, unspecified\n" +
                    "\n" +
                    "1, primary reference (e.g.,, calibrated atomic clock,, radio clock)\n" +
                    "\n" +
                    "2-255, secondary reference (via NTP)");

            Log.i("TimeThread", "Message:Poll: " + timeInfo.getMessage().getPoll());
            Log.i("TimeThread","Poll Interval: This is an eight-bit signed integer indicating the\n" +
                    "maximum interval between successive messages, in seconds to the nearest\n" +
                    "power of two. The values that can appear in this field range from\n" +
                    "NTP.MINPOLL to NTP.MAXPOLL inclusive.");

            Log.i("TimeThread", "Message:Version: " + timeInfo.getMessage().getVersion());
            Log.i("TimeThread", "Message:TransmitTimeStamp: " + timeInfo.getMessage().getTransmitTimeStamp().toUTCString());

            Log.i("TimeThread", "Message:LeapIndicator: " + timeInfo.getMessage().getLeapIndicator());
            Log.i("TimeThread", "Leap Indicator (sys.leap, peer.leap, pkt.leap): This is a two-bit code\n" +
                    "warning of an impending leap second to be inserted in the NTP timescale.\n" +
                    "The bits are set before 23:59 on the day of insertion and reset after\n" +
                    "00:00 on the following day. This causes the number of seconds (rollover\n" +
                    "interval) in the day of insertion to be increased or decreased by one.\n" +
                    "In the case of primary servers the bits are set by operator\n" +
                    "intervention, while in the case of secondary servers the bits are set by\n" +
                    "the protocol. The two bits, bit 0 and bit 1, respectively, are coded as\n" +
                    "follows:\n" +
                    "@Z_TBL_BEG = COLUMNS(2), DIMENSION(IN), COLWIDTHS(E1,E8), WIDTH(5.0000),\n" +
                    "ABOVE(.0830), BELOW(.0830), HGUTTER(.0560), KEEP(OFF), ALIGN(CT)\n" +
                    "\n" +
                    "@Z_TBL_BODY = TABLE TEXT, TABLE TEXT\n" +
                    "\n" +
                    "00, no warning\n" +
                    "\n" +
                    "01, last minute has 61 seconds\n" +
                    "\n" +
                    "10, last minute has 59 seconds\n" +
                    "\n" +
                    "11, alarm condition (clock not synchronized)\n" +
                    "\n" +
                    "@Z_TBL_END =\n");

            Log.i("TimeThread", "Message:RootDelayInMillisDouble: " + timeInfo.getMessage().getRootDelayInMillisDouble());
            Log.i("TimeThread", "Root Delay (sys.rootdelay, peer.rootdelay, pkt.rootdelay): This is a\n" +
                    "signed fixed-point number indicating the total roundtrip delay to the\n" +
                    "primary reference source at the root of the synchronization subnet, in\n" +
                    "seconds. Note that this variable can take on both positive and negative\n" +
                    "values, depending on clock precision and skew.");

            Log.i("TimeThread", "Message:Datagram:Port: " + timeInfo.getMessage().getDatagramPacket().getPort());
            Log.i("TimeThread", "Message:Datagram:Offset: " + timeInfo.getMessage().getDatagramPacket().getOffset());

        } catch (Exception e) {

            Log.e("TimeThread", "Error while printing info.");

        }
    }

    private class NTPObservable extends Observable{

        public void setChanged(){

            this.setChanged();
        }

    }
}


