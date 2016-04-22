package com.example.andersson.musicapp.Instrument;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

/**
 * Created by Andersson on 07/04/16.
 */
public class BassThread extends AbstractInstrumentThread {

    long startTime = 0;
    long sampleTime = 0;
    private int soundId1;
    private int soundId2;
    private int soundId3;
    private int soundId4;
    private int soundId5;
    private int soundId6;
    private int soundId7;
    private int soundId8;

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

        if (playRealTime) {

            soundCase(value);

        } else {

            sampleTime = (long) (((double) getLoopTime() / (double) getBars()) * 1000);

            if (System.currentTimeMillis() - startTime > sampleTime) {

                soundCase(value);
                startTime = System.currentTimeMillis();
            }
        }
    }

    protected void initiateSound() {

        soundId1 = holder.getSoundPool().load(activity.getContext(), R.raw.piano1, 1);
        soundId2 = holder.getSoundPool().load(activity.getContext(), R.raw.piano2, 1);
        soundId3 = holder.getSoundPool().load(activity.getContext(), R.raw.piano3, 1);
        soundId4 = holder.getSoundPool().load(activity.getContext(), R.raw.piano4, 1);
        soundId5 = holder.getSoundPool().load(activity.getContext(), R.raw.piano1, 1);
        soundId6 = holder.getSoundPool().load(activity.getContext(), R.raw.piano2, 1);
        soundId7 = holder.getSoundPool().load(activity.getContext(), R.raw.piano3, 1);
        soundId8 = holder.getSoundPool().load(activity.getContext(), R.raw.piano4, 1);

    }

    private void soundCase(int value) {

        if (value == 0) {

            holder.getSoundPool().play(soundId1, volume, volume, 1, 0, 1f);

        } else if (value == 1) {

            holder.getSoundPool().play(soundId2, volume, volume, 1, 0, 1f);

        } else if (value == 2) {

            holder.getSoundPool().play(soundId3, volume, volume, 1, 0, 1f);

        } else if (value == 3) {

            holder.getSoundPool().play(soundId4, volume, volume, 1, 0, 1f);

        } else if (value == 4) {

            holder.getSoundPool().play(soundId5, volume, volume, 1, 0, 1f);

        } else if (value == 5) {

            holder.getSoundPool().play(soundId6, volume, volume, 1, 0, 1f);

        } else if (value == 6) {

            holder.getSoundPool().play(soundId7, volume, volume, 1, 0, 1f);

        } else if (value == 7) {

            holder.getSoundPool().play(soundId8, volume, volume, 1, 0, 1f);
        }


    }
}
