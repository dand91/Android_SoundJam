package com.example.andersson.musicapp.AsyncUpdate;

import android.util.Log;

import java.util.HashMap;
import java.util.Observable;

/**
 * Created by Andersson on 07/04/16.
 */
public class UpdateObservable extends Observable {

    public UpdateObservable() {
        super();
    }

    public void setChange(HashMap arg) {

        Log.d("UpdateObservable", "Number of observers - " + this.countObservers());
        notifyObservers(arg);
        setChanged();

    }

}
