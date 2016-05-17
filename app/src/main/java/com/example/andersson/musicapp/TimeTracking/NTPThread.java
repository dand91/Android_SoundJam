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

    private static final int UPDATE_TIME = 60000;
    private static final int TIMEOUT = 2000;

    private long adjust = 0;
    private long offset = 0;
    private long returnTime;
    private TimeInfo timeInfo;
    private NTPObservable observable;
    private ArrayList<Long> timeList;

    public NTPThread() {

        this.observable = new NTPObservable();
        this.timeList = new ArrayList<Long>();

    }

    @Override
    public void run() {

        while (true) {

            returnTime = getNTPUpdate();

            if (returnTime != Integer.MAX_VALUE) {

                timeInfo.computeDetails();
                adjust = returnTime - System.currentTimeMillis();
                offset = timeInfo.getOffset();
                timeList.add(adjust);

                try {

                    observable.NTPnotify(offset);

                } catch (NullPointerException e) {

                    Log.e("NTPThread", "Offset is null using adjustment");

                    observable.NTPnotify(adjust);

                }
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
                timeClient.setVersion(4);

                Log.d("NTPThread", "Starting NTP version: " + timeClient.getVersion() + " Server: " + server);

                timeClient.setDefaultTimeout(TIMEOUT);

                InetAddress inetAddress = InetAddress.getByName(server);
                timeInfo = timeClient.getTime(inetAddress);

                return timeInfo.getMessage().getTransmitTimeStamp().getTime();

            } catch (UnknownHostException e1) {
                Log.e("NTPThread", "URL error");
                Log.e("NTPThread", "Message: " + e1.getMessage());
            } catch (java.io.IOException e2) {
                Log.e("NTPThread", "Connection error");
                Log.e("NTPThread", "Message: " + e2.getMessage());

            }
        }

        return Integer.MAX_VALUE;
    }

    public synchronized long getAdjustment() {

        return adjust;
    }

    public synchronized long getOffset() {

        return offset;
    }

    public synchronized long getTime() {

        return returnTime;
    }

    public synchronized double getMean() {

        double n = timeList.size();
        double sumTime = 0;

        for (Long time : timeList) {

            sumTime += (double) time;

        }

        return sumTime / n;
    }

    public synchronized double getVariance() {

        double n = timeList.size();
        double mean = getMean();
        double sumTime = 0;

        for (Long time : timeList) {

            sumTime += Math.pow(((double) time - mean), 2);

        }

        return sumTime / n;
    }


    public void addObserver(Observer observer) {

        observable.addObserver(observer);
    }

    /**
     * Method for printing information. Commends above each output is taken from rfc1305
     */
    public void getInfo() {

        try {

            Log.i("NTPThread", "Adjustment: " + adjust);
            Log.i("NTPThread", "TimeMean: " + getMean());
            Log.i("NTPThread", "TimeVariance: " + getVariance());
            Log.i("NTPThread", "ReturnTime: " + timeInfo.getReturnTime());
            Log.i("NTPThread", "DelayTime: " + timeInfo.getDelay());
            Log.i("NTPThread", "Offset: " + timeInfo.getOffset());

            /*
            Modes of Operation

            Except in broadcast mode, an NTP association is formed when two peers
            exchange messages and one or both of them create and maintain an
            instantiation of the protocol machine, called an association. The
            association can operate in one of five modes as indicated by the host-
            mode variable (peer.mode): symmetric active, symmetric passive, client,
            server and broadcast, which are defined as follows:

            Symmetric Active (1): A host operating in this mode sends periodic
            messages regardless of the reachability state or stratum of its peer. By
            operating in this mode the host announces its willingness to synchronize
            and be synchronized by the peer.

            Symmetric Passive (2): This type of association is ordinarily created
            upon arrival of a message from a peer operating in the symmetric active
            mode and persists only as long as the peer is reachable and operating at
            a stratum level less than or equal to the host; otherwise, the
            association is dissolved. However, the association will always persist
            until at least one message has been sent in reply. By operating in this
            mode the host announces its willingness to synchronize and be
            synchronized by the peer.

            Client (3): A host operating in this mode sends periodic messages
            regardless of the reachability state or stratum of its peer. By
            operating in this mode the host, usually a LAN workstation, announces
            its willingness to be synchronized by, but not to synchronize the peer.

            Server (4): This type of association is ordinarily created upon arrival
            of a client request message and exists only in order to reply to that
            request, after which the association is dissolved. By operating in this
            mode the host, usually a LAN time server, announces its willingness to
            synchronize, but not to be synchronized by the peer.

            Broadcast (5): A host operating in this mode sends periodic messages
            regardless of the reachability state or stratum of the peers. By
            operating in this mode the host, usually a LAN time server operating on
            a high-speed broadcast medium, announces its willingness to synchronize
            all of the peers, but not to be synchronized by any of them.

            A host operating in client mode occasionally sends an NTP message to a
            host operating in server mode, perhaps right after rebooting and at
            periodic intervals thereafter. The server responds by simply
            interchanging addresses and ports, filling in the required information
            and returning the message to the client. Servers need retain no state
            information between client requests, while clients are free to manage
            the intervals between sending NTP messages to suit local conditions. In
            these modes the protocol machine described in this document can be
            considerably simplified to a simple remote-procedure-call mechanism
            without significant loss of accuracy or robustness, especially when
            operating over high-speed LANs.

            In the symmetric modes the client/server distinction (almost)
            disappears. Symmetric passive mode is intended for use by time servers
            operating near the root nodes (lowest stratum) of the synchronization
            subnet and with a relatively large number of peers on an intermittent
            basis. In this mode the identity of the peer need not be known in
            advance, since the association with its state variables is created only
            when an NTP message arrives. Furthermore, the state storage can be
            reused when the peer becomes unreachable or is operating at a higher
            stratum level and thus ineligible as a synchronization source.

            Symmetric active mode is intended for use by time servers operating near
            the end nodes (highest stratum) of the synchronization subnet. Reliable
            time service can usually be maintained with two peers at the next lower
            stratum level and one peer at the same stratum level, so the rate of
            ongoing polls is usually not significant, even when connectivity is lost
            and error messages are being returned for every poll.

            Normally, one peer operates in an active mode (symmetric active, client
            or broadcast modes) as configured by a startup file, while the other
            operates in a passive mode (symmetric passive or server modes), often
            without prior configuration. However, both peers can be configured to
            operate in the symmetric active mode. An error condition results when
            both peers operate in the same mode, but not symmetric active mode. In
            such cases each peer will ignore messages from the other, so that prior
            associations, if any, will be demobilized due to reachability failure.

            Broadcast mode is intended for operation on high-speed LANs with
            numerous workstations and where the highest accuracies are not required.
            In the typical scenario one or more time servers on the LAN send
            periodic broadcasts to the workstations, which then determine the time
            on the basis of a preconfigured latency in the order of a few
            milliseconds. As in the client/server modes the protocol machine can be
            considerably simplified in this mode; however, a modified form of the
            clock selection algorithm may prove useful in cases where multiple time
            servers are used for enhanced reliability.
             */
            Log.i("NTPThread", "Message:ModeName: " + timeInfo.getMessage().getModeName());
            Log.i("NTPThread", "Message:Mode: " + timeInfo.getMessage().getMode());

            /*
            Precision (sys.precision, peer.precision, pkt.precision): This is a
            signed integer indicating the precision of the various clocks, in
            seconds to the nearest power of two. The value must be rounded to the
            next larger power of two; for instance, a 50-Hz (20 ms) or 60-Hz (16.67
            ms) power-frequency clock would be assigned the value -5 (31.25 ms),
            while a 1000-Hz (1 ms) crystal-controlled clock would be assigned the
            value -9 (1.95 ms).
             */
            Log.i("NTPThread", "Message:Precision: " + timeInfo.getMessage().getPrecision());

            /*
            Root Dispersion (sys.rootdispersion, peer.rootdispersion,
            pkt.rootdispersion): This is a signed fixed-point number indicating the
            maximum error relative to the primary reference source at the root of
            the synchronization subnet, in seconds. Only positive values greater
            than zero are possible.
             */
            Log.i("NTPThread", "Message:RootDispersionInMillis: " + timeInfo.getMessage().getRootDispersionInMillis());

            /*
            Stratum (sys.stratum, peer.stratum, pkt.stratum): This is an integer
            indicating the stratum of the local clock, with values defined as
            follows:

            @Z_TBL_BEG = COLUMNS(2), DIMENSION(IN), COLWIDTHS(E1,E8), WIDTH(5.0000),
            ABOVE(.0830), BELOW(.0830), HGUTTER(.0560), KEEP(OFF), ALIGN(CT)

            @Z_TBL_BODY = TABLE TEXT, TABLE TEXT

            0, unspecified

            1, primary reference (e.g.,, calibrated atomic clock,, radio clock)

            2-255, secondary reference (via NTP)

            @Z_TBL_END =
             */
            Log.i("NTPThread", "Message:Stratum: " + timeInfo.getMessage().getStratum());

            /*
            Poll Interval: This is an eight-bit signed integer indicating the
            maximum interval between successive messages, in seconds to the nearest
            power of two. The values that can appear in this field range from
            NTP.MINPOLL to NTP.MAXPOLL inclusive.
             */
            Log.i("NTPThread", "Message:Poll: " + timeInfo.getMessage().getPoll());

            /*
            Leap Indicator (LI): This is a two-bit code warning of an impending leap
            second to be inserted/deleted in the last minute of the current day,
            with bit 0 and bit 1, respectively, coded as follows:

            @Z_TBL_BEG = COLUMNS(2), DIMENSION(IN), COLWIDTHS(E1,E8), WIDTH(5.0000),
            ABOVE(.0830), BELOW(.0830), HGUTTER(.0560), KEEP(OFF), ALIGN(CT)

            @Z_TBL_BODY = TABLE TEXT, TABLE TEXT

            00, no warning

            01, last minute has 61 seconds

            10, last minute has 59 seconds)

            11, alarm condition (clock not synchronized)

            @Z_TBL_END =
             */
            Log.i("NTPThread", "Message:LeapIndicator: " + timeInfo.getMessage().getLeapIndicator());

           /*
            Root Delay (sys.rootdelay, peer.rootdelay, pkt.rootdelay): This is a
            signed fixed-point number indicating the total roundtrip delay to the
            primary reference source at the root of the synchronization subnet, in
            seconds. Note that this variable can take on both positive and negative
            values, depending on clock precision and skew.
             */
            Log.i("NTPThread", "Message:RootDelayInMillisDouble: " + timeInfo.getMessage().getRootDelayInMillisDouble());

            Log.i("NTPThread", "Message:Type: " + timeInfo.getMessage().getType());
            Log.i("NTPThread", "Message:ReferenceIdString: " + timeInfo.getMessage().getReferenceIdString());
            Log.i("NTPThread", "Message:Version: " + timeInfo.getMessage().getVersion());
            Log.i("NTPThread", "Message:TransmitTimeStamp: " + timeInfo.getMessage().getTransmitTimeStamp().toUTCString());
            Log.i("NTPThread", "Message:Datagram:Port: " + timeInfo.getMessage().getDatagramPacket().getPort());
            Log.i("NTPThread", "Message:Datagram:Offset: " + timeInfo.getMessage().getDatagramPacket().getOffset());

        } catch (Exception e) {

            Log.e("NTPThread", "Error while printing info.");

        }
    }

    private class NTPObservable extends Observable {

        public void NTPnotify(long value) {

            notifyObservers(value);
            setChanged();
        }

    }
}


