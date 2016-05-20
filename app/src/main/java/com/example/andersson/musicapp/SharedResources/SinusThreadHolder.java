package com.example.andersson.musicapp.SharedResources;

import com.example.andersson.musicapp.AsyncUpdate.UpdateThread;
import com.example.andersson.musicapp.Instrument.SinusThread;
import com.example.andersson.musicapp.TimeTracking.TimeThread;

import java.util.HashMap;

/**
 * Created by Andersson on 20/05/16.
 */
public class SinusThreadHolder {

    private static SinusThreadHolder instance = null;
    private SinusThread sinusThread = null;

    public static SinusThreadHolder getInstance() {

        if (instance == null) {
            instance = new SinusThreadHolder();
        }
        return instance;
    }

    public SinusThread getSinusThread(){

        return sinusThread;
    }

    public void setSinusThread(SinusThread sinusThread){

        this.sinusThread = sinusThread;
        sinusThread.start();
    }

    public boolean hasSinusTread(){

        return (sinusThread == null)?false:true;
    }
}
