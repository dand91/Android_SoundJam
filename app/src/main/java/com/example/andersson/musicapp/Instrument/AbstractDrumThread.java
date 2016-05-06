package com.example.andersson.musicapp.Instrument;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;

/**
 * Created by Andersson on 07/04/16.
 */
public abstract class AbstractDrumThread extends AbstractInstrumentThread {

    public AbstractDrumThread(AbstractInstrumentActivity activity, ThreadHolder holder) {
        super(activity, holder);
    }

    protected abstract void initiateSound();

    @Override
    public void playLoop(int index) {

        if (soundList != null && soundList.size() > index && soundList.get(index) == 1 && !record && !playRealTime) {

            sph.getSoundPool().play(soundId, volume, volume, 1, 0, 1f);

        }
    }

    public void playRealTime(int value) {

        sph.getSoundPool().play(soundId, volume, volume, 1, 0, 1f);

    }

    @Override
    protected void setBeat() {

        int k = 1;

        for(Integer i : soundList){

            if(i == 1) {
                holder.setBeatArray(activity.getName(), k, true);
            }else{
                holder.setBeatArray(activity.getName(), k, false);
            }
            k++;
        }

    }
}