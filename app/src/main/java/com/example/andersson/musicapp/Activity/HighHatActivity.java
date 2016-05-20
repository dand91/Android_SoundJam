package com.example.andersson.musicapp.Activity;

import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.Instrument.HighHatThread;

public class HighHatActivity extends AbstractDrumActivity {


    // GUI/Instrument code
    public HighHatActivity() {
        super();
    }

    @Override
    public String getName() { // Set the name, mostly for thread separation

        return "HighHat";
    }

    @Override
    public AbstractInstrumentThread getInstrumentClass() {// Return corresponding playLoop that the activity should use
        return new HighHatThread(this);
    }
}