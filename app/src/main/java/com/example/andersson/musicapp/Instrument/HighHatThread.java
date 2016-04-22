package com.example.andersson.musicapp.Instrument;


import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

/**
 * Created by Andersson on 07/04/16.
 */
public class HighHatThread extends AbstractDrumThread {

    public HighHatThread(AbstractInstrumentActivity activity, SharedInfoHolder holder) {
        super(activity, holder);
    }

    protected void initiateSound() {

        soundId = holder.getSoundPool().load(activity.getContext(), R.raw.hh02, 1);

    }

}
