package com.example.andersson.musicapp;


import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Andersson on 07/04/16.
 */
public class ExampleInstrumentThread2 extends InstrumentThread {

    private int soundId;

    public ExampleInstrumentThread2(InstrumentActivity activity, ThreadHolder holder) {
        super(activity, holder);

        if(activity == null){

            Log.d("EIT2", "Constructor activity is null");

        }
    }

    @Override
    public void instrument(int index) {


        if(tempSoundList != null &&  tempSoundList.size() > index && tempSoundList.get(index) == 1 ) {

            AudioManager mgr = (AudioManager) activity.getContext().getSystemService(Context.AUDIO_SERVICE);
            int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
            streamVolume = streamVolume / mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            holder.getSoundPool().play(soundId, 0.5f, 0.5f, 1, 0, 1f);
        }
    }

    public void initiate(){

        if(activity != null) {

            if(activity.getContext() != null) {

                if(holder.getSoundPool() != null) {

                    soundId = holder.getSoundPool().load(activity.getContext(), R.raw.bd2, 1);

                }else{

                    Log.d("EIT2","soundPool is null");
                    System.exit(0);
                }
            }else{

                Log.d("EIT2","Context is null");
            }

        }else{

            Log.d("EIT2","Actvity is null");
        }
    }
}
