package com.example.andersson.musicapp.SharedResources;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andersson on 12/05/16.
 */
public class BeatHolder {

    private static BeatHolder instance = null;
    private HashMap<String, Boolean> beat;
    private Activity activity;

    public BeatHolder() {

        this.beat = new HashMap<String, Boolean>();

    }

    public static BeatHolder getInstance() {

        if (instance == null) {
            instance = new BeatHolder();
        }
        return instance;
    }


    public HashMap<String, Boolean> getBeatMap() {

        return beat;
    }

    public void setBeatArray(String instrument, int index, boolean on) {

        if (!beat.containsKey(instrument + index)) {
            beat.put(instrument + index, on);
        } else {
            beat.remove(instrument + index);
            beat.put(instrument + index, on);
        }
    }

    public void clearBeatArray() {


        try {

            for (Map.Entry beatTemp : beat.entrySet()) {

                    beat.put((String)beatTemp.getKey(),false);

            }

        }catch(Exception e){

            e.printStackTrace();
        }
    }

    public void addActivity(Activity activity) {

        this.activity = activity;
    }

    public Activity getActivity() {

        return activity;
    }
}
