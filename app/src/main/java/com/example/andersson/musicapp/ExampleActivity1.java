package com.example.andersson.musicapp;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ExampleActivity1 extends InstrumentActivity{

    public ExampleActivity1(){
        super();
    }

    @Override
    void generateSoundInfo() {

        int min = 200;
        int max = 500;

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        soundList.add(randomNum);

    }

    @Override
    String setName() {

        return "ExampleActivity1";
    }

    @Override
    void initiate() {
        instrument.setSoundList(new ArrayList<Integer>(Arrays.asList(200, 210, 220, 230, 240, 250, 260, 270)));
        instrument.setBars(8);
        instrument.setLoopTime(16);
    }

    @Override
    InstrumentThread getInstrumentClass() {
        return new ExampleInstrument();
    }
}