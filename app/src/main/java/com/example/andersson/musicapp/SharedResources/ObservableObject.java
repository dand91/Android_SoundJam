package com.example.andersson.musicapp.SharedResources;

import java.util.Observable;

/**
 * Created by Andersson on 07/04/16.
 */
public class ObservableObject extends Observable {

    public void setChange(){

        notifyObservers();
        setChanged();

    }

    public void setChange(Object object){

        notifyObservers(object);
        setChanged();

    }
}
