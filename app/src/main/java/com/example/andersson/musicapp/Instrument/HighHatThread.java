package com.example.andersson.musicapp.Instrument;


import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;

/**
 * Created by Andersson on 07/04/16.
 */
public class HighHatThread extends AbstractDrumThread {

    public HighHatThread(AbstractInstrumentActivity activity, ThreadHolder holder) {
        super(activity, holder);
    }

    protected void initiateSound() {

        soundId = sph.getSoundPool().load(activity.getBaseContext(), R.raw.hh02, 1);

    }

}
