package com.example.andersson.musicapp.TimeTracking;

import android.util.Log;

import java.util.Observable;

/**
 * Created by Andersson on 07/04/16.
 */
public class TimeObservable extends Observable {

    public TimeObservable() {
        super();
    }

    public void setChange() {

        Log.d("TimeObservable", "Number of observers - " + this.countObservers());
        notifyObservers();
        setChanged();

    }
}
