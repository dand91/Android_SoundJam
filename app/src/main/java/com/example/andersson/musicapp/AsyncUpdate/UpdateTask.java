package com.example.andersson.musicapp.AsyncUpdate;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.andersson.musicapp.Activity.MainActivity;
import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.Pool.ThreadPool;
import com.example.andersson.musicapp.SharedResources.MainHolder;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateTask {


    private static final String[] SERVER_ADDRESS = {"http://213.21.69.152:1234/test"};
    private static final Integer TIMEOUT_TIME = 2000;

    /**
     * Class for sending and retrieving data from HTTP server.
     *
     * @param ob - SharedInfoHolder to get information from threads. UpdateObservable to
     *           notify threads.
     * @return String - Dummy string
     */
    public static String saveAndLoad(UpdateObservable ob) {

        MainHolder mainHolder = MainHolder.getInstance();
        ThreadHolder threadHolder = ThreadHolder.getInstance();
        MainActivity tempMain = (MainActivity) mainHolder.getMainActivity();

        try {

            String tempGroupName = mainHolder.getGroupName();

            if (tempGroupName.equals("noName") || tempGroupName.equals("")) {

                return "";

            }

            Log.i("UpdateTask", "Collecting info from threads from group name: " + tempGroupName);


        } catch (NullPointerException e) {

            Log.e("UpdateTask", "Holder is null");
            System.exit(0);
        }

        if (!haveNetworkConnection(tempMain)) { // Check internet connection

            Log.e("UpdateTask", "No internet connection");
            tempMain.CreateDialog("No internet connection");

        } else {

            ThreadPool.getInstance().getInfo();

            HttpURLConnection conn = initiateConnection(SERVER_ADDRESS);
            SendClassList scl = collectDataFromThreads(mainHolder, threadHolder);
            sendXMLData(conn, scl, mainHolder);
            receiveXMLData(conn, ob, mainHolder);

            conn.disconnect();

        }

        return "";
    }

    /**
     * Initiates a URL connection through the class HttpURLConnection, sets variables to make it a
     * POST request.
     *
     * @param addressList - String address to server
     * @return conn - The connection object
     */
    private static HttpURLConnection initiateConnection(String[] addressList) {

        for (int i = 0; i < addressList.length; i++) {

            String address = addressList[i];

            try {

                URL url = new URL(address); // URL to HTTP server

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(TIMEOUT_TIME);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");

                return conn;

            } catch (MalformedURLException e1) {

                Log.e("UpdateTask", "Error while connecting to server.");
                Log.e("UpdateTask", "Message1: " + e1.getMessage());
                e1.printStackTrace();

            } catch (ProtocolException e2) {

                Log.e("UpdateTask", "Error while connecting to server.");
                Log.e("UpdateTask", "Message1: " + e2.getMessage());
                e2.printStackTrace();

            } catch (IOException e3) {

                Log.e("UpdateTask", "Error while connecting to server.");
                Log.e("UpdateTask", "Message1: " + e3.getMessage());
                e3.printStackTrace();
            }
        }


        Log.e("UpdateTask", "Server connection loop finished, shutting down.");
        System.exit(1);

        return null;
    }

    /**
     * Collects data from the active threads. Creates a list of SendClass objects to be converted to
     * XML string.
     *
     * @param mainHolder,threadHolder - Object containing the threads.
     * @return scl - The list of SendClass objects.
     */
    private static SendClassList collectDataFromThreads(MainHolder mainHolder, ThreadHolder threadHolder) {

        HashMap<String, Thread> threads = threadHolder.getThreads();
        SendClassList scl = new SendClassList();

        String groupName = mainHolder.getGroupName();
        int BPM = mainHolder.getBPM();

        scl.setGroupName(groupName);
        scl.setBPM(BPM);

        if (!threads.entrySet().isEmpty()) {

            try {

                /*
                    Retrieve information from the program. This should be adapted to you're SendClass
                    and SendCalssList
                */
                for (Map.Entry<String, Thread> entry : threads.entrySet()) {

                    AbstractInstrumentThread tempThread = (AbstractInstrumentThread) entry.getValue();
                    ArrayList<Integer> soundList = tempThread.getSoundList();
                    String tempString = soundList.toString();

                    SendClass temp = new SendClass();

                    if (tempThread.getChangeStatus() && tempString.length() > 2) {


                        tempThread.setChangedStatus(false);

                        tempString = soundList.toString().substring(1, tempString.length() - 1).replace(" ", "");
                        temp.setHasData(true);
                        temp.setInstrumentName(entry.getKey());
                        temp.setData(tempString);
                        temp.setVolume(String.valueOf(tempThread.getVolume()));
                        temp.setBars((int) tempThread.getBars());

                    } else {

                        temp.setInstrumentName("N/I");
                        temp.setData("N/I");
                        temp.setVolume("N/I");
                        temp.setBars(0);
                        temp.setHasData(false);
                    }

                    scl.getSendClassList().add(temp);

                }

            } catch (Exception e) {

                Log.e("UpdateTask", "Error while adding info from threads.");
                Log.e("UpdateTask", "Message2.1: " + e.getMessage());
                e.printStackTrace();
                System.exit(0);

            }

        } else {

            try {

                SendClass temp = new SendClass();
                temp.setInstrumentName("N/I");
                temp.setData("N/I");
                temp.setVolume("N/I");
                temp.setBars(0);
                temp.setHasData(false);
                scl.getSendClassList().add(temp);

            } catch (Exception e) {
                Log.e("UpdateTask", "Error while adding group info.");
                Log.e("UpdateTask", "Message2.2: " + e.getMessage());
                System.exit(0);
            }
        }
        return scl;
    }

    /**
     * Uses the created conn object to fill the data field of the POST request with the data
     * given in scl (SendClassList) which is marshalled to XML.
     *
     * @param conn,scl
     */
    private static void sendXMLData(HttpURLConnection conn, SendClassList scl, MainHolder mainHolder) {

        try {

            StringWriter writer = new StringWriter();

            Serializer serializerWrite =
                    new Persister(); // XML converter

            serializerWrite.write(scl, writer);
            String temp = writer.toString();

            Log.v("UpdateTask", "Sending: " + temp);

            OutputStreamWriter wr = new OutputStreamWriter(conn
                    .getOutputStream());
            wr.write(temp);
            wr.flush();
            wr.close();

        } catch (IOException e1) {

            Log.e("UpdateTask", "Error while sending info.");
            Log.e("UpdateTask", "Message 3: " + e1.getMessage());
            e1.printStackTrace();

            ((MainActivity) mainHolder.getMainActivity()).CreateDialog("Unable to connect to server, try again later.");

        } catch (Exception e2) {

            Log.e("UpdateTask", "Error while converting to XML.");
            Log.e("UpdateTask", "Message 3: " + e2.getMessage());
            e2.printStackTrace();
            System.exit(0);
        }

    }

    /**
     * Uses the created conn object to retrieve data from the HTTP server. The XML data is
     * demarshalled and sent to the corresponding thread through Observer pattern.
     *
     * @param conn,ob,threadHolder
     */
    private static void receiveXMLData(HttpURLConnection conn, UpdateObservable ob, MainHolder mainHolder) {

        String response = "";

        try {


            BufferedReader br = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();

            while ((line = br.readLine()) != null) {
                responseOutput.append(line);
            }

            br.close();

            response = responseOutput.toString();

            Log.v("UpdateTask", "Receiving: " + response);

        } catch (IOException e) {

            Log.e("UpdateTask", "Error while fetching info.");
            Log.e("UpdateTask", "Message4.1: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);

        }

        if (response.length() > 0) {


            try {

                Serializer serializerOut = new Persister();

                SendClassList sc = serializerOut.read(SendClassList.class, response);

                int k = 0;

                int BPMTemp = sc.getBPM();
                mainHolder.setBPM(BPMTemp);

                String info = "";

                for (SendClass scTemp : sc.getSendClassList()) {

                    /*
                        Retrieve information from xml below. This should be adapted to you're SendClass
                        and SendCalssList.
                     */

                    k++;
                    String instrumentNameTemp = scTemp.getInstrumentName();
                    String dataTemp = scTemp.getData();
                    String volumeTemp = scTemp.getVolume();
                    Integer barsTemp = scTemp.getBars();

                    ArrayList<Integer> list = new ArrayList<Integer>();

                    for (String s : dataTemp.split(",")) {

                        list.add(Integer.valueOf(s));

                    }

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("soundList", list);
                    map.put("volume", (Float.valueOf(volumeTemp)) / 100);
                    map.put("instrumentName", instrumentNameTemp);
                    map.put("bars", barsTemp);

                    ob.setChange(map);

                    Log.i("UpdateTask", "Result fetch: " + instrumentNameTemp + " " + list.toString());
                    info = info + "\nAdded: " + instrumentNameTemp;
                }

                if (k > 0) {

                    mainHolder.setInfoText("Info fetched from database from group: " + sc.getGroupName() + info);
                }

            } catch (Exception e) {
                Log.e("UpdateTask", "Error while parsing fetched info.");
                Log.e("UpdateTask", "Message4.2: " + e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }

        } else {

            try {

                Log.d("UpdateTask", "Error in received header: ");
                Log.d("UpdateTask", "Error code: " + conn.getResponseCode());

            } catch (Exception e) {

                Log.e("UpdateTask", "Error while fetching header info.");
                Log.e("UpdateTask", "Message5: " + e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    /**
     * Method for checking if WIFI or other network connection is established.
     *
     * @return true - if network connection exists, otherwise false.
     */
    private static boolean haveNetworkConnection(Activity mainActivity) {

        if (mainActivity != null) {

            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager) mainActivity.getSystemService(mainActivity.getApplicationContext().CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }

            return haveConnectedWifi || haveConnectedMobile;

        } else {

            Log.e("UpdateTask", "Main activity is null when checking network connection.");
            return false;

        }
    }
}
