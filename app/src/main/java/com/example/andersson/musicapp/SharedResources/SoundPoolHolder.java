package com.example.andersson.musicapp.SharedResources;

import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by Andersson on 27/04/16.
 */
public class SoundPoolHolder {

    private static SoundPoolHolder instance = null;
    private SoundPool mySound;

    public SoundPoolHolder() {

        this.mySound = new SoundPool(30, AudioManager.STREAM_MUSIC, 0);

    }

    public static SoundPoolHolder getInstance() {

        if (instance == null) {

            instance = new SoundPoolHolder();
        }
        return instance;
    }

    public SoundPool getSoundPool() {
        return mySound;
    }
}
