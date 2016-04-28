package com.example.andersson.musicapp.SharedResources;

import android.util.Log;

import java.util.Observable;

/**
 * Created by Andersson on 26/04/16.
 */
public class AbstractObservable extends Observable {

    public void setChange() {

        Log.d("AbstractObservable", "Number of observers " + this.countObservers() + " - " + this.getClass());
        notifyObservers();
        setChanged();

    }

    public void setChange(Object arg) {

        Log.d("AbstractObservable", "Number of observers " + this.countObservers() + " - " + this.getClass());
        notifyObservers(arg);
        setChanged();

    }
}
