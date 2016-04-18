package com.example.andersson.musicapp.Instrument;

import android.util.Log;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

/**
 * Created by Andersson on 07/04/16.
 */
public class ExampleInstrumentThread4 extends AbstractInstrumentThread {

    private int soundId;
    private int soundId1;
    private int soundId2;
    private int soundId3;
    private int soundId4;

    long startTime = 0;
    long sampleTime = 100;

    public ExampleInstrumentThread4(AbstractInstrumentActivity activity, SharedInfoHolder holder) {
        super(activity, holder);
    }

    @Override
    public void playLoop(int index) {

    }

    public void playRealTime(int value) {

        //Play real time audio

        if (System.currentTimeMillis() - startTime > sampleTime) {


            if (value == 0) {

                holder.getSoundPool().play(soundId1, volume, volume, 1, 0, 1f);
                Log.d("realTimeTest", "0");

            } else if (value == 1) {

                holder.getSoundPool().play(soundId2, volume, volume, 1, 0, 1f);
                Log.d("realTimeTest", "1");

            } else if (value == 2) {

                holder.getSoundPool().play(soundId3, volume, volume, 1, 0, 1f);
                Log.d("realTimeTest", "2");

            } else if (value == 3) {

                holder.getSoundPool().play(soundId4, volume, volume, 1, 0, 1f);
                Log.d("realTimeTest", "3");
            }

            startTime = System.currentTimeMillis();
        }

    }

    protected void initiateSound() {

        soundId1 = holder.getSoundPool().load(activity.getContext(), R.raw.piano1, 1);
        soundId2 = holder.getSoundPool().load(activity.getContext(), R.raw.piano2, 1);
        soundId3 = holder.getSoundPool().load(activity.getContext(), R.raw.piano3, 1);
        soundId4 = holder.getSoundPool().load(activity.getContext(), R.raw.piano4, 1);

    }


}
