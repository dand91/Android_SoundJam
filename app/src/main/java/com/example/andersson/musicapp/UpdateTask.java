package com.example.andersson.musicapp;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateTask {

    public static String saveAndLoad(ThreadHolder holder) {

        String tempGroupName = holder.getGroupName();

        Log.d("UpdateTask", "Collecting info from threads" );

        String tempInfo = "";

        if(holder == null) {

            Log.d("UpdateTask", "Holder is null");

        }else if(!haveNetworkConnection(holder.getMainActivity())){

            Log.d("UpdateTask", "No internet connection");

        } else {

            HashMap<String, Thread> threads = holder.getThreads();


            if (threads.size() > 0) {

                int j = 0;
                for (Map.Entry<String, Thread> entry : threads.entrySet()) {

                    ArrayList<Integer> soundList = ((InstrumentThread) entry.getValue()).getSoundList();

                    if (soundList.size() > 0) {

                        if(j == 0) {
                            tempInfo = tempInfo + entry.getKey() + "-";
                        }else{
                            tempInfo = tempInfo + ":" + entry.getKey() + "-";
                        }
                        j++;

                        int i = 0;
                        for (Integer in : soundList) {

                            if(i == soundList.size()-1) {
                                tempInfo = tempInfo + in;
                            }else{
                                tempInfo = tempInfo + in + ",";
                            }
                            i++;
                        }

                    }
                }
            }

            InputStream is = null;

            Log.d("UpdateTask", "Sending request: " + tempInfo);

            URL url;

            try {

                url = new URL("http://213.21.69.152:1234/test");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                OutputStreamWriter wr = new OutputStreamWriter(conn
                        .getOutputStream());
                // this is were we're adding post data to the request

                String group = "group=" + tempGroupName;
                String info = "&info=" + tempInfo;

                wr.write(group + info);
                wr.flush();
                wr.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();

                String response = responseOutput.toString();

                Log.d("UpdateTask Result: ", response);

                if (response.length() > 0){

                for (String resultString : response.split(":")) {

                    String[] subresult = resultString.split("-");
                    ArrayList<Integer> list = new ArrayList<Integer>();

                    for (String s : subresult[1].split(",")) {

                        list.add(Integer.valueOf(s));

                    }

                    InstrumentThread tempThreads = ((InstrumentThread) holder.getThread(subresult[0]));

                    if (tempThreads != null) {

                        tempThreads.setSoundList(list);

                    } else {

                        Log.d("UpdateTask", "Thread is null for: " + subresult[0]);

                    }

                    Log.d("UpdateTask Result2", subresult[0] + " " + list.toString());
                }

            }
            } catch (Exception e) {
                Log.e("UpdateTask", e.getMessage());
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
