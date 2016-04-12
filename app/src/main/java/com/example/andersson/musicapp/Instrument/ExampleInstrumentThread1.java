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
public class ExampleInstrumentThread1 extends AbstractInstrumentThread {

    private int soundId;
    long startTime;
    long sampleTime;

    public ExampleInstrumentThread1(AbstractInstrumentActivity activity, SharedInfoHolder holder) {
        super(activity, holder);
        startTime = 0;
        sampleTime = 1000;
    }

    @Override
    public void playLoop(int index) {

        if(tempSoundList != null &&  tempSoundList.size() > index && tempSoundList.get(index) == 1 ) {

            AudioManager mgr = (AudioManager) activity.getContext().getSystemService(Context.AUDIO_SERVICE);
            int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
            streamVolume = streamVolume / mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            holder.getSoundPool().play(soundId, streamVolume, streamVolume, 1, 0, 1f);

        }else{

        }
    }

    public void playRealTime(int value){

        //Play real time audio

        if(System.currentTimeMillis() -  startTime > sampleTime) {

            if (value == 0) {

                Log.d("realTimeTest","0");

            } else if (value == 1) {

                Log.d("realTimeTest","1");

            } else if (value == 2) {

                Log.d("realTimeTest","2");

            } else if (value == 3) {
                Log.d("realTimeTest","3");
            }

            startTime = System.currentTimeMillis();
        }

    }

    public void initiate(){

        if(activity != null) {

            if(activity.getContext() != null) {

                if(holder.getSoundPool() != null) {

                    soundId = holder.getSoundPool().load(activity.getContext(), R.raw.bd, 1);

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
