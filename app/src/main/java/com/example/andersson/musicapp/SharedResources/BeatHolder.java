package com.example.andersson.musicapp.SharedResources;

import java.util.HashMap;

/**
 * Created by Andersson on 12/05/16.
 */
public class BeatHolder {

    private HashMap<String, Boolean> beat;
    private static BeatHolder instance = null;

    public BeatHolder(){

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

        beat.clear();

    }

}
