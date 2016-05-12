package com.example.andersson.musicapp.Instrument;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;

/**
 * Created by Andersson on 07/04/16.
 */
public class BassThread extends AbstractInstrumentThread {

    private int value = 0;
    private int soundId1;
    private int soundId2;
    private int soundId3;
    private int soundId4;
    private int soundId5;
    private int soundId6;
    private int soundId7;
    private int soundId8;

    public BassThread(AbstractInstrumentActivity activity) {
        super(activity);

    }

    @Override
    public void playLoop(int index) {

        if (soundList != null && soundList.size() > index && !record) {

            if(!playRealTime) {

                int loopValue = soundList.get(index);
                soundCase(loopValue);

            }else{

                soundCase(value);

            }

        }else if(record){

            soundCase(value);

        }
    }

    public void playRealTime(int value) {

        this.value = value;

    }

    protected void initiateSound() {

        soundId1 = sph.getSoundPool().load(activity.getBaseContext(), R.raw.piano1, 1);
        soundId2 = sph.getSoundPool().load(activity.getBaseContext(), R.raw.piano2, 1);
        soundId3 = sph.getSoundPool().load(activity.getBaseContext(), R.raw.piano3, 1);
        soundId4 = sph.getSoundPool().load(activity.getBaseContext(), R.raw.piano4, 1);
        soundId5 = sph.getSoundPool().load(activity.getBaseContext(), R.raw.piano1, 1);
        soundId6 = sph.getSoundPool().load(activity.getBaseContext(), R.raw.piano2, 1);
        soundId7 = sph.getSoundPool().load(activity.getBaseContext(), R.raw.piano3, 1);
        soundId8 = sph.getSoundPool().load(activity.getBaseContext(), R.raw.piano4, 1);

    }

    private void soundCase(int value) {

        float tempvolume = volume / 15f;

        if (value == 0) {

            sph.getSoundPool().play(soundId1, tempvolume, tempvolume, 1, 0, 1f);

        } else if (value == 1) {

            sph.getSoundPool().play(soundId2, tempvolume, tempvolume, 1, 0, 1f);

        } else if (value == 2) {

            sph.getSoundPool().play(soundId3, tempvolume, tempvolume, 1, 0, 1f);

        } else if (value == 3) {

            sph.getSoundPool().play(soundId4, tempvolume, tempvolume, 1, 0, 1f);

        } else if (value == 4) {

            sph.getSoundPool().play(soundId5, tempvolume, tempvolume, 1, 0, 1f);

        } else if (value == 5) {

            sph.getSoundPool().play(soundId6, tempvolume, tempvolume, 1, 0, 1f);

        } else if (value == 6) {

            sph.getSoundPool().play(soundId7, tempvolume, tempvolume, 1, 0, 1f);

        } else if (value == 7) {

            sph.getSoundPool().play(soundId8, tempvolume, tempvolume, 1, 0, 1f);
        }


    }

    @Override
    protected void setBeat() {

    }
}
