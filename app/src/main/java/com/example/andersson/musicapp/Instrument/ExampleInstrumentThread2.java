package com.example.andersson.musicapp.Instrument;


import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

/**
 * Created by Andersson on 07/04/16.
 */
public class ExampleInstrumentThread2 extends AbstractInstrumentThread {

    private int soundId;

    public ExampleInstrumentThread2(AbstractInstrumentActivity activity, SharedInfoHolder holder) {

        super(activity, holder);

    }

    @Override
    public void playLoop(int index) {

        if(tempSoundList != null &&  tempSoundList.size() > index && tempSoundList.get(index) == 1 ) {

            AudioManager mgr = (AudioManager) activity.getContext().getSystemService(Context.AUDIO_SERVICE);
            int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
            streamVolume = streamVolume / mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            holder.getSoundPool().play(soundId, volume, volume, 1, 0, 1f);

        }else{

        }
    }

    public void playRealTime(int value){

        //Play real time audio

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

    public void setVolume(float volume){

        this.volume = volume;

    }

    public int getVolume(){

        return (int) (volume*100);
    }
}