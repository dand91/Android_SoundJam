package com.example.andersson.musicapp.SharedResources;

import android.util.Log;

import java.util.Observable;

/**
 * Created by Andersson on 07/04/16.
 */
public class TimeObservable extends AbstractObservable {

    public TimeObservable(){
        super();
    }

    public void setChange() {

        Log.d("AbstractObservable", "Number of observers" + this.countObservers() + " - " + this.getClass());
        notifyObservers();
        setChanged();

    }
}
