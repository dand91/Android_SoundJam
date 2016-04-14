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
    private int soundId1;
    private int soundId2;
    private int soundId3;
    private int soundId4;

    long startTime;
    long sampleTime;

    public ExampleInstrumentThread1(AbstractInstrumentActivity activity, SharedInfoHolder holder) {
        super(activity, holder);
        startTime = 0;
        sampleTime = 100;
    }

    @Override
    public void playLoop(int index) {

        if(tempSoundList != null &&  tempSoundList.size() > index && tempSoundList.get(index) == 1 ) {

            holder.getSoundPool().play(soundId, volume, volume, 1, 0, 1f);

        }else{

        }
    }

    public void playRealTime(int value){

        //Play real time audio

        if(System.currentTimeMillis() -  startTime > sampleTime) {


            if (value == 0) {

                holder.getSoundPool().play(soundId1, volume, volume, 1, 0, 1f);
                Log.d("realTimeTest","0");

            } else if (value == 1) {

                holder.getSoundPool().play(soundId2, volume, volume, 1, 0, 1f);
                Log.d("realTimeTest","1");

            } else if (value == 2) {

                holder.getSoundPool().play(soundId3, volume, volume, 1, 0, 1f);
                Log.d("realTimeTest","2");

            } else if (value == 3) {

                holder.getSoundPool().play(soundId4, volume, volume, 1, 0, 1f);
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
                    soundId1 = holder.getSoundPool().load(activity.getContext(), R.raw.piano1, 1);
                    soundId2 = holder.getSoundPool().load(activity.getContext(), R.raw.piano2, 1);
                    soundId3 = holder.getSoundPool().load(activity.getContext(), R.raw.piano3, 1);
                    soundId4 = holder.getSoundPool().load(activity.getContext(), R.raw.piano4, 1);

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

        return  (int) (volume*100);

    }

}
