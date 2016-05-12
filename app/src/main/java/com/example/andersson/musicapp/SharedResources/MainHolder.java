package com.example.andersson.musicapp.SharedResources;

import android.app.Activity;

import com.example.andersson.musicapp.Activity.MainActivity;

/**
 * Created by Andersson on 12/05/16.
 */
public class MainHolder {

    private static MainHolder instance = null;
    private Activity mainActivity;

    public static MainHolder getInstance() {

        if (instance == null) {
            instance = new MainHolder();
        }
        return instance;
    }

    public String getGroupName() {

        return ((MainActivity) mainActivity).getGroupName();
    }


    public Activity getMainActivity() {

        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


}
