package com.example.andersson.musicapp.AsyncUpdate;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateTask {

    public static String saveAndLoad(SharedInfoHolder holder) {

        String tempGroupName = holder.getGroupName();

        Log.d("UpdateTask", "Collecting info from threads from group name: "  + holder.getGroupName());


        if(holder == null) { // Check if SharedInfoHolder is active

            Log.d("UpdateTask", "Holder is null");

        }else if(!haveNetworkConnection(holder.getMainActivity())) { // Check internet connection

            Log.d("UpdateTask", "No internet connection");

        } else { // Run HTTP POST

            InputStream is = null;

            URL url = null;
            OutputStreamWriter wr = null;
            HttpURLConnection conn = null;

            try {

                url = new URL("http://213.21.69.152:1234/test"); // URL to HTTP server

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                wr = new OutputStreamWriter(conn
                        .getOutputStream());


            } catch (Exception e) {

                Log.e("UpdateTask", "Message1: " + e.getMessage());
            }

            HashMap<String, Thread> threads = holder.getThreads();
            threads = holder.getThreads();

            StringWriter writer = new StringWriter();

            Serializer serializer = new Persister(); // XML converter

            SendClassList scl = new SendClassList();

            if (!threads.entrySet().isEmpty()){ // If there is data to send

                try {

                        for (Map.Entry<String, Thread> entry : threads.entrySet()) {

                            AbstractInstrumentThread tempThread = (AbstractInstrumentThread) entry.getValue();
                            ArrayList<Integer> soundList = tempThread.getSoundList();
                            String tempString = soundList.toString();

                            if (tempString.length() > 2){

                                tempString =  soundList.toString().substring(1,tempString.length()-1).replace(" ","");

                            }else{

                                tempString = "N/I";

                            }

                            SendClass temp = new SendClass();
                            temp.setGroupName(holder.getGroupName());
                            temp.setInstrumentName(entry.getKey());
                            temp.setData(tempString);


                            temp.setVolume(String.valueOf(tempThread.getVolume()));

                            scl.getSendClassList().add(temp);
                        }

                    }catch(Exception e){
                        Log.e("UpdateTask", "Message2: " + e.getMessage());
                    }

                }else{ // Send for information from server

                    SendClass temp = new SendClass();
                    temp.setGroupName(holder.getGroupName());
                    temp.setInstrumentName("N/I");
                    temp.setData("N/I");
                    temp.setVolume("N/I");
                    scl.getSendClassList().add(temp);
                }

            String response = "";

            try{

                serializer.write(scl, writer);
                String temp = writer.toString();

                Log.d("UpdateTask","Sending: " + temp);

                wr.write(temp);
                wr.flush();
                wr.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();

                response = responseOutput.toString();

                Log.d("UpdateTask", "Receiving: " + response);

            } catch (Exception e) {

                Log.e("UpdateTask", "Message3: " + e.getMessage());

            }

                if (response.length() > 0){

                    try {

                        serializer = new Persister();
                    SendClassList sc = null;


                        sc = serializer.read(SendClassList.class, response);


                int k = 0;

                for (SendClass scTemp : sc.getSendClassList()) {

                    String instrumentNameTemp = scTemp.getInstrumentName();
                    String infoTemp = scTemp.getData();
                    String volumeTemp = scTemp.getVolume();

                    ArrayList<Integer> list = new ArrayList<Integer>();

                    for (String s : infoTemp.split(",")) {

                        list.add(Integer.valueOf(s));

                    }

                    AbstractInstrumentThread tempThreads = ((AbstractInstrumentThread) holder.getThread(instrumentNameTemp));

                    if (tempThreads != null) {

                        tempThreads.setSoundList(list);
                        tempThreads.setVolume((Float.valueOf(volumeTemp))/100);

                    } else {

                        Log.d("UpdateTask", "Thread not found for name: " + instrumentNameTemp);

                    }

                    Log.d("UpdateTask", "Result fetch: " + instrumentNameTemp + " " + list.toString());
                }

                    } catch (Exception e) {

                        Log.e("UpdateTask", "Message4: " + e.getMessage());
                    }

            }

        }


        return "";
    }

    private static boolean haveNetworkConnection(Activity mainActivity) {
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
    }

}
