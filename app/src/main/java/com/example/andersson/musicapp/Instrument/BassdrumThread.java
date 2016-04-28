package com.example.andersson.musicapp.Instrument;

import com.example.andersson.musicapp.Activity.AbstractInstrumentActivity;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

/**
 * Created by Andersson on 07/04/16.
 */
public class BassdrumThread extends AbstractDrumThread {

    public BassdrumThread(AbstractInstrumentActivity activity, SharedInfoHolder holder) {
        super(activity, holder);

    }

    protected void initiateSound() {

        soundId = sph.getSoundPool().load(activity.getBaseContext(), R.raw.bd, 1);

    }
}
