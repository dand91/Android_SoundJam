package com.example.andersson.musicapp.Activity;


import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.Instrument.SnareThread;

public class SnareActivity extends AbstractDrumActivity {


    // GUI/Instrument code
    public SnareActivity() {
        super();
    }

    @Override
    public String getName() { // Set the name, mostly for thread separation

        return "SnareActivity";
    }

    @Override
    public AbstractInstrumentThread getInstrumentClass() {// Return corresponding playLoop that the activity should use
        return new SnareThread(this, holder);
    }
}