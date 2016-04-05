package com.example.andersson.musicapp;

import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by Andersson on 05/04/16.
 */
public class ExampleInstrument extends InstrumentThread {
    @Override
    public void instrument(int index) {

        this.genTone(index);
        this.playSound();
        Log.d("Play", "Playing");
    }
}
