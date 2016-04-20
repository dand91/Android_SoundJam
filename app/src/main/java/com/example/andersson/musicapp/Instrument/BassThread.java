package com.example.andersson.musicapp.Instrument;

import android.util.Log;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

/**
 * Created by Andersson on 07/04/16.
 */
public class BassThread extends AbstractInstrumentThread {

    private int soundId1;
    private int soundId2;
    private int soundId3;
    private int soundId4;

    long startTime = 0;
    long sampleTime = 0;

    public BassThread(AbstractInstrumentActivity activity, SharedInfoHolder holder) {
        super(activity, holder);

    }

    @Override
    public void playLoop(int index) {

        if (soundList != null && soundList.size() > index && !record && !playRealTime) {

            int value = soundList.get(index);

            if (value == 0) {

                holder.getSoundPool().play(soundId1, volume, volume, 1, 0, 1f);

            } else if (value == 1) {

                holder.getSoundPool().play(soundId2, volume, volume, 1, 0, 1f);

            } else if (value == 2) {

                holder.getSoundPool().play(soundId3, volume, volume, 1, 0, 1f);

            } else if (value == 3) {

                holder.getSoundPool().play(soundId4, volume, volume, 1, 0, 1f);

            }
        }
    }

    public void playRealTime(int value) {

        sampleTime = (long) (getLoopTime()/getBars());

        if (System.currentTimeMillis() - startTime > sampleTime) {


            if (value == 0) {

                holder.getSoundPool().play(soundId1, volume, volume, 1, 0, 1f);

            } else if (value == 1) {

                holder.getSoundPool().play(soundId2, volume, volume, 1, 0, 1f);

            } else if (value == 2) {

                holder.getSoundPool().play(soundId3, volume, volume, 1, 0, 1f);

            } else if (value == 3) {

                holder.getSoundPool().play(soundId4, volume, volume, 1, 0, 1f);
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
