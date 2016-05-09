package com.example.andersson.musicapp.SharedResources;

import android.util.Log;

/**
 * Created by Andersson on 07/04/16.
 */
public class UpdateObservable extends AbstractObservable {

    public UpdateObservable() {
        super();
    }

    public void setChange(Object arg) {

        Log.d("UpdateObservable", "Number of observers - " + this.countObservers() + " - " + this.getClass());
        notifyObservers(arg);
        setChanged();

    }

}
