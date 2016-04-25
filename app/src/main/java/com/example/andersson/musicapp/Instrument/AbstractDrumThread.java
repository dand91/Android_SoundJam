package com.example.andersson.musicapp.Instrument;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

/**
 * Created by Andersson on 07/04/16.
 */
public abstract class AbstractDrumThread extends AbstractInstrumentThread {

    protected int soundId;


    protected abstract void initiateSound();

    public AbstractDrumThread(AbstractInstrumentActivity activity, SharedInfoHolder holder) {
        super(activity, holder);
    }

    @Override
    public void playLoop(int index) {

        if (soundList != null && soundList.size() > index && soundList.get(index) == 1 && !record && !playRealTime) {

            holder.getSoundPool().play(soundId, volume, volume, 1, 0, 1f);

        }
    }

    public void playRealTime(int value) {

        holder.getSoundPool().play(soundId, volume, volume, 1, 0, 1f);

    }


}