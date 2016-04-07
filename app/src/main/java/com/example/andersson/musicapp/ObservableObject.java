package com.example.andersson.musicapp;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Andersson on 07/04/16.
 */
public class ObservableObject extends Observable {

    public void setChange(){

        notifyObservers();
        setChanged();

    }
}
