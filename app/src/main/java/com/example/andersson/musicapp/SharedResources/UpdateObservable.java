package com.example.andersson.musicapp.SharedResources;

import android.util.Log;

import java.util.Observable;

/**
 * Created by Andersson on 07/04/16.
 */
public class UpdateObservable extends Observable {

    public UpdateObservable() {
        super();
    }

    public void setChange(Object arg) {

        Log.d("UpdateObservable", "Number of observers - " + this.countObservers());
        notifyObservers(arg);
        setChanged();

    }

}
