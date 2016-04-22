package com.example.andersson.musicapp.Activity;

import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.Instrument.BassdrumThread;

public class BassdrumActivity extends AbstractDrumActivity {


    // GUI/Instrument code
    public BassdrumActivity() {
        super();
    }

    @Override
    public String getName() { // Set the name, mostly for thread separation

        return "BassdrumActivity";
    }

    @Override
    public AbstractInstrumentThread getInstrumentClass() {// Return corresponding playLoop that the activity should use
        return new BassdrumThread(this, holder);
    }
}