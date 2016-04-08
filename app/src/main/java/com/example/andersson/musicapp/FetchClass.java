package com.example.andersson.musicapp;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchClass {
    public static String getResultFromRss(ThreadHolder holder) {

        Log.d("FetchClass", "Collecting info from threads" );

        String info = "info=";

        if(holder == null){

            Log.d("FetchClass", "NULL" );

        }else {

            HashMap<String, Thread> threads = holder.getThreads();


            if (threads.size() > 0) {

                int j = 0;
                for (Map.Entry<String, Thread> entry : threads.entrySet()) {

                    ArrayList<Integer> soundList = ((InstrumentThread) entry.getValue()).getSoundList();

                    if (soundList.size() > 0) {

                        if(j == 0) {
                            info = info + entry.getKey() + "-";
                        }else{
                            info = info + ":" + entry.getKey() + "-";
                        }
                        j++;

                        int i = 0;
                        for (Integer in : soundList) {

                            if(i == soundList.size()-1) {
                                info = info + in;
                            }else{
                                info = info + in + ",";
                            }
                            i++;
                        }

                    }
                }
            }


            InputStream is = null;

            Log.d("FetchClass", "Sending request: " + info);

            URL url;
            try {
                url = new URL("http://213.21.69.152:1234/test");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                OutputStreamWriter wr = new OutputStreamWriter(conn
                        .getOutputStream());
                // this is were we're adding post data to the request
                wr.write(info);
                wr.flush();
                wr.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();
                Log.d("FetchClass Result: ", responseOutput.toString());

                for (String resultString : responseOutput.toString().split(":")) {

                    String[] subresult = resultString.split("-");
                    ArrayList<Integer> list = new ArrayList<Integer>();

                    for (String s : subresult[1].split(",")) {

                        list.add(Integer.valueOf(s));
                        ((InstrumentThread) holder.getThread(subresult[0])).setSoundList(list);
                    }
                    Log.d("FetchClass Restult2", subresult[0] + " " + list.toString());
                }


            } catch (Exception e) {
                Log.e("FetchClass", e.getMessage());
            }


        }
        return "";
    }
}
