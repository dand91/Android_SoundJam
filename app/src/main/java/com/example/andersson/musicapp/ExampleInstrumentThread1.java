package com.example.andersson.musicapp;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

/**
 * Created by Andersson on 07/04/16.
 */
public class ExampleInstrumentThread1 extends InstrumentThread {

    private SoundPool mySound;
    private int soundId;

    public ExampleInstrumentThread1(InstrumentActivity activity, TimeThread thread) {
        super(activity, thread);

        if(activity == null){

            Log.d("EIT2", "Constructor activity is null");

        }
    }

    @Override
    public void instrument(int index) {

        AudioManager mgr = (AudioManager) activity.getContext().getSystemService(Context.AUDIO_SERVICE);
        int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        streamVolume = streamVolume / mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mySound.play(soundId, streamVolume, streamVolume, 1, 0, 1f);
    }

    public void initiate(){

        mySound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        if(activity != null) {

            if(activity.getContext() != null) {

                soundId = mySound.load(activity.getContext(), R.raw.bd, 1);

            }else{

                Log.d("EIT2","Context is null");
            }
        }else{
            Log.d("EIT2","Actvity is null");
        }
    }
}
