package com.example.andersson.musicapp.Instrument;


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

        if (soundList != null && soundList.size() > index && soundList.get(index) == 1) {

            holder.getSoundPool().play(soundId, volume, volume, 1, 0, 1f);

        }
    }

    public void playRealTime(int value) {

        //Play real time audio

    }

    protected void initiateSound() {


        soundId = holder.getSoundPool().load(activity.getContext(), R.raw.bd2, 1);

    }

}
