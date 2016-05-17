package com.example.andersson.musicapp.Instrument;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.SharedResources.BeatHolder;

/**
 * Created by Andersson on 07/04/16.
 */
public abstract class AbstractDrumThread extends AbstractInstrumentThread {

    private BeatHolder beatHolder;

    public AbstractDrumThread(AbstractInstrumentActivity activity) {
        super(activity);
        this.beatHolder = BeatHolder.getInstance();
    }

    protected abstract void initiateSound();

    @Override
    public void playLoop(int index) {

        if (soundList != null && soundList.size() > index && soundList.get(index) == 1 && !record && !playRealTime && !pause) {

            sph.getSoundPool().play(soundId, volume, volume, 1, 0, 1f);

        }
    }

    public void playRealTime(int value) {

        sph.getSoundPool().play(soundId, volume, volume, 1, 0, 1f);

    }

    @Override
    protected void setBeat() {

        if (!soundList.isEmpty() && soundList.get(0) != -1) {

            int k = 1;

            for (int i = 0; i < 8; i++) {

                if (soundList.get(i) == 1) {

                    beatHolder.setBeatArray(activity.getName(), k, true);

                } else {

                    beatHolder.setBeatArray(activity.getName(), k, false);

                }

                k++;
            }
        }
    }

    @Override
    public double getBars() {

        if (soundList.size() == 0) {

            return 16;

        } else {

            return bars;

        }

    }
}