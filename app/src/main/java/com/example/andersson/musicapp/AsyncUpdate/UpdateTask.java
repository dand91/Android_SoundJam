package com.example.andersson.musicapp.AsyncUpdate;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.andersson.musicapp.Activity.MainActivity;
import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;
import com.example.andersson.musicapp.SharedResources.UpdateObservable;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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

    private static UpdateObservable ob;


    public static String saveAndLoad(SharedInfoHolder holder, UpdateObservable ob) {


        String tempGroupName = holder.getGroupName();

        Log.d("UpdateTask", "Collecting info from threads from group name: " + tempGroupName);


        if (holder == null) { // Check if SharedInfoHolder is active

            Log.d("UpdateTask", "Holder is null");
            System.exit(0);

        } else if (!haveNetworkConnection(holder.getMainActivity())) { // Check internet connection

            Log.d("UpdateTask", "No internet connection");
            ((MainActivity) holder.getMainActivity()).AlertNoInternet();

        } else { // Run HTTP POST

            InputStream is = null;

            URL url = null;
            HttpURLConnection conn = null;

            try {

                url = new URL("http://213.21.69.152:1234/test"); // URL to HTTP server

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");

            } catch (MalformedURLException e1) {

                Log.e("UpdateTask", "Error while connecting to server.");
                Log.e("UpdateTask", "Message1: " + e1.getMessage());
                System.exit(0);

            } catch (ProtocolException e2) {

                Log.e("UpdateTask", "Error while connecting to server.");
                Log.e("UpdateTask", "Message1: " + e2.getMessage());
                System.exit(0);

            } catch (IOException e3) {

                Log.e("UpdateTask", "Error while connecting to server.");
                Log.e("UpdateTask", "Message1: " + e3.getMessage());
                System.exit(0);
            }

            HashMap<String, Thread> threads = holder.getThreads();
            threads = holder.getThreads();

            SendClassList scl = new SendClassList();


            Log.d("UpdateTask", "Threads available");

            if (!threads.entrySet().isEmpty()) { // If there is data to send

                try {

                    for (Map.Entry<String, Thread> entry : threads.entrySet()) {

                        AbstractInstrumentThread tempThread = (AbstractInstrumentThread) entry.getValue();
                        ArrayList<Integer> soundList = tempThread.getSoundList();
                        String tempString = soundList.toString();

                        if (tempString.length() > 2) {

                            tempString = soundList.toString().substring(1, tempString.length() - 1).replace(" ", "");

                        } else {

                            tempString = "N/I";

                        }

                        SendClass temp = new SendClass();
                        temp.setGroupName(holder.getGroupName());
                        temp.setInstrumentName(entry.getKey());
                        temp.setData(tempString);

                        temp.setVolume(String.valueOf(tempThread.getVolume()));

                        scl.getSendClassList().add(temp);
                    }

                } catch (Exception e) {
                    Log.e("UpdateTask", "Error while adding info from threads.");
                    Log.e("UpdateTask", "Message2.1: " + e.getMessage());
                    System.exit(0);

                }

            } else { // Send for information from server

                try {

                    SendClass temp = new SendClass();
                    temp.setGroupName(holder.getGroupName());
                    temp.setInstrumentName("N/I");
                    temp.setData("N/I");
                    temp.setVolume("N/I");

                    scl.getSendClassList().add(temp);

                } catch (Exception e) {
                    Log.e("UpdateTask", "Error while adding group info.");
                    Log.e("UpdateTask", "Message2.2: " + e.getMessage());
                    System.exit(0);
                }
            }

            StringWriter writer = new StringWriter();

            Serializer serializerWrite =
                    new Persister(); // XML converter

            OutputStreamWriter wr = null;

            try {

                serializerWrite.write(scl, writer);
                String temp = writer.toString();

                Log.d("UpdateTask", "Sending: " + temp);

                wr = new OutputStreamWriter(conn
                        .getOutputStream());
                wr.write(temp);
                wr.flush();
                wr.close();

            } catch (IOException e1) {

                Log.e("UpdateTask", "Error while sending info.");
                Log.e("UpdateTask", "Message2.3: " + e1.getMessage());
                System.exit(0);

            } catch (Exception e2) {

                Log.e("UpdateTask", "Error while converting to XML.");
                Log.e("UpdateTask", "Message2.3: " + e2.getMessage());
                System.exit(0);
            }


            String response = "";
            BufferedReader br = null;

            try {


                br = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }

                br.close();

                response = responseOutput.toString();

                Log.d("UpdateTask", "Receiving: " + response);

            } catch (IOException e) {

                Log.e("UpdateTask", "Error while fetching info.");
                Log.e("UpdateTask", "Message4: " + e.getMessage());
                System.exit(0);

            }

            if (response.length() > 0) {

                Serializer serializerOut = new Persister();
                SendClassList sc = null;

                try {

                    sc = serializerOut.read(SendClassList.class, response);

                    int k = 0;

                    for (SendClass scTemp : sc.getSendClassList()) {

                        String instrumentNameTemp = scTemp.getInstrumentName();
                        String dataTemp = scTemp.getData();
                        String volumeTemp = scTemp.getVolume();

                        ArrayList<Integer> list = new ArrayList<Integer>();

                        for (String s : dataTemp.split(",")) {

                            list.add(Integer.valueOf(s));

                        }

                        AbstractInstrumentThread tempThreads = ((AbstractInstrumentThread) holder.getThread(instrumentNameTemp));

                        if (tempThreads != null) {

                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("soundList", list);
                            map.put("volume", (Float.valueOf(volumeTemp)) / 100);
                            map.put("instrumentName", instrumentNameTemp);
                            ob.setChange(map);

                        } else {

                            Log.d("UpdateTask", "Thread not found for name: " + instrumentNameTemp);

                        }

                        Log.d("UpdateTask", "Result fetch: " + instrumentNameTemp + " " + list.toString());
                    }

                } catch (Exception e) {
                    Log.e("UpdateTask", "Error while parsing fetched info.");
                    Log.e("UpdateTask", "Message4: " + e.getMessage());
                    System.exit(0);
                }

            } else {

                try {

                    Log.d("UpdateTask", "Error in received header: ");
                    Log.d("UpdateTask", "Error code: " + conn.getResponseCode());

                } catch (Exception e) {

                    Log.e("UpdateTask", "Error while fetching header info.");
                    Log.e("UpdateTask", "Message5: " + e.getMessage());
                    System.exit(0);
                }

            }

            conn.disconnect();
        }


        return "";
    }

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

            Log.d("UpdateTask", "Main activity is null when checking network connection.");
            return false;

        }
    }
}
