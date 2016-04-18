package com.example.andersson.musicapp.SharedResources;

import java.util.Observable;

/**
 * Created by Andersson on 07/04/16.
 */
public class UpdateObservable extends Observable {

    public void setChange() {

        notifyObservers();
        setChanged();

    }

    public void setChange(Object arg) {

        notifyObservers(arg);
        setChanged();

    }

    public void setChange(Observable o, Object arg) {

        notifyObservers(arg);
        setChanged();

    }
}
