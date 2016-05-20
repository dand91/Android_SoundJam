package com.example.andersson.musicapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.andersson.musicapp.Instrument.AbstractDrumThread;
import com.example.andersson.musicapp.Instrument.AbstractInstrumentThread;
import com.example.andersson.musicapp.Pool.ThreadPool;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.BeatHolder;
import com.example.andersson.musicapp.SharedResources.MainHolder;
import com.example.andersson.musicapp.SharedResources.SoundPoolHolder;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class AbstractInstrumentActivity extends BaseActivity {


    public Button recordButton;
    public Button playButton;
    public Button stopButton;
    public Button removeButton;
    public Button speedButton;

    public ThreadHolder threadHolder;
    public BeatHolder beatHolder;
    public MainHolder mainHolder;
    public AbstractInstrumentThread instrument;
    public TextView soundListText;
    public TextView speedText;
    public TextView progressText;
    public SeekBar volumeSeekBar;
    public boolean playRealTime;
    public boolean record;
    protected SoundPoolHolder soundPoolHolder;
    protected ArrayList<Integer> tempSoundList;
    private int index = 0;
    private String info;

    abstract void generateSoundInfo(ArrayList<Integer> list, int index);

    public abstract String getName();

    protected abstract void initiate();

    protected abstract void initiateGUI();

    protected abstract AbstractInstrumentThread getInstrumentClass();

    protected abstract int getLayout();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());


        info = null;
        threadHolder = ThreadHolder.getInstance();
        beatHolder = BeatHolder.getInstance();
        mainHolder = MainHolder.getInstance();
        soundPoolHolder = SoundPoolHolder.getInstance();


        try {

            Intent i = getIntent();

            info = i.getStringExtra("backInfo");

        } catch (NullPointerException e) {

            Log.d("AbstractInstrument", "Error loading intent");
            System.exit(0);
        }

        if (info != null) {

            if (info.equals("back")) {

                Intent myIntent = new Intent();
                setResult(RESULT_OK, myIntent);
                finish();

            }

        } else {

            Log.e("AbstractInstrument", "BackInfo is null");

        }

        String name = getName();

        if (threadHolder != null) {

            if (threadHolder.containsKey(name)) {

                instrument = (AbstractInstrumentThread) threadHolder.getThread(name);
                Log.d("AbstractInstrument", "New playLoop fetched");


            } else {

                instrument = getInstrumentClass();
                threadHolder.addThread(name, instrument);
                Log.d("AbstractInstrument", "New playLoop created");

            }

        } else if (threadHolder == null) {

            Log.d("AbstractInstrument", "Holder is null - " + name);
            System.exit(0);

        }

        initiate();
        initiateGUI();

    }

    protected void recordGUI() {

        recordButton = (Button) findViewById(R.id.recordButton);
        progressText = (TextView) findViewById(R.id.progressText);

        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ThreadPool threadPool = ThreadPool.getInstance();

                if (!record) {

                    soundListText.setText("");
                    recordButton.setClickable(false);

                    while (true) {

                        Calendar calendar = Calendar.getInstance();
                        int second = calendar.get(Calendar.SECOND) + 1;
                        int millisecond = calendar.get(Calendar.MILLISECOND) + 1;
                        int time = (int) (Math.round((second * 1000 + millisecond) / 10.0) * 10);
                        final int tempLoopTime = (int) (Math.round((((MainActivity) mainHolder.getMainActivity()).getLoopTime() * 1000) / 10.0) * 10);
                        final double bars = 8;
                        instrument.setBars(8);

                        if (instrument instanceof AbstractDrumThread) {

                            speedText.setText("Speed: 8 bars");

                        }

                        if (time % tempLoopTime == 0) {

                            index = 0;
                            tempSoundList = new ArrayList<Integer>();


                            Thread tempThread = new Thread() {

                                public void run() {

                                    record = true;
                                    instrument.setRecord(true);

                                    try {

                                        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

                                        for (int i = 4; i >= 0; i--) {

                                            final int countDown = i;

                                            runOnUiThread(() -> {

                                                progressText.setText("Record start in: " + countDown);


                                            });

                                            for(int j = 0 ; j < 2; j++ ){

                                                sleep(200);
                                                v.vibrate(50);
                                                sleep((long) ((tempLoopTime / bars)) - 200);

                                            }

                                            instrument.setBars(8);
                                            instrument.setChangedStatus(true);

                                        }


                                        while (true) {

                                            index++;

                                            runOnUiThread(() -> {

                                                progressText.setText("Beat " + index + " of " + bars);
                                                generateSoundInfo(tempSoundList, index);


                                            });

                                            sleep(200);
                                            v.vibrate(50);

                                            if (index == 8) {

                                                break;
                                            }

                                            sleep((long) ((tempLoopTime / bars)) - 200);

                                        }

                                    } catch (InterruptedException e) {

                                        e.printStackTrace();
                                    }


                                    runOnUiThread(() -> {

                                        playRealTime = false;
                                        instrument.setPlayRealTime(false);
                                        record = false;
                                        instrument.setRecord(false);
                                        progressText.setText("Updating server");

                                        for (int i = 0; i < 10; i++) {

                                            instrument.setSoundList(tempSoundList);
                                            instrument.setChangedStatus(true);

                                            try {

                                                sleep(CLIENT_UPDATE_TIME / 10);

                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        recordButton.setClickable(true);

                                    });


                                }

                            };

                            threadPool.add(tempThread, "record");
                            break;
                        }
                    }
                }
            }
        });
    }

    protected void volumeGUI() {

        volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setProgress(instrument.getVolume());
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


                runOnUiThread(() -> {

                    int volume = volumeSeekBar.getProgress();

                    for (int i = 0; i < 10; i++) {

                        instrument.setVolume(((float) volume) / 100);
                        instrument.setChangedStatus(true);

                        try {

                            Thread.sleep(CLIENT_UPDATE_TIME / 10);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                });

            }

        });
    }

    protected void stopPlayGUI() {

        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                playRealTime = true;
                instrument.setPlayRealTime(true);
                Log.d("AbstractInstrument", "playRealTime");
            }
        });
        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                playRealTime = false;
                instrument.setPlayRealTime(false);

                Log.d("AbstractInstrument", "stopRealTime");
            }
        });
    }

    protected void removeGUI() {

        removeButton = (Button) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {


                ThreadPool threadPool = ThreadPool.getInstance();

                Thread removeThread = new Thread() {

                    @Override
                    public void run() {

                        runOnUiThread(() -> {

                            if (!instrument.getSoundList().isEmpty()) {

                                ArrayList<Integer> tempList = new ArrayList<Integer>();
                                tempList.add(-1);
                                soundListText.setText("");

                                for (int i = 0; i < 10; i++) {

                                    instrument.setSoundList(tempList);
                                    instrument.setChangedStatus(true);
                                    beatHolder.clearBeatArray();

                                    try {

                                        sleep(CLIENT_UPDATE_TIME / 10);

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                progressText.setText("Instrument removed.");

                            }

                        });
                    }
                };

                threadPool.add(removeThread, "removeThread");

            }
        });
    }

    protected void speedGUI() {

        speedText = (TextView) findViewById(R.id.speedText);
        if (instrument.getBars() == 8 && instrument.getSoundList().size() == 8) {
            speedText.setText("Speed: 8 bars");
        } else if (instrument.getBars() == 16 && instrument.getSoundList().size() == 16) {
            speedText.setText("Speed: 16 bars");
        }

        speedButton = (Button) findViewById(R.id.speedButton);
        speedButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                progressText.setText("Updating server");

                if (instrument.getBars() == 8 && instrument.getSoundList().size() == 8) {


                    final ArrayList<Integer> tempList = instrument.getSoundList();

                    if (tempList.size() == 8) {

                        speedText.setText("Speed: 16 bars");


                        ArrayList<Integer> newList = new ArrayList<Integer>();
                        newList.addAll(tempList);
                        newList.addAll(tempList);

                        speedButton.setEnabled(false);
                        speedButton.setClickable(false);

                        for (int i = 0; i < 10; i++) {

                            instrument.setBars(16);
                            instrument.setSoundList(newList);
                            instrument.setChangedStatus(true);

                            try {

                                Thread.sleep(CLIENT_UPDATE_TIME / 10);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        speedButton.setEnabled(true);
                        speedButton.setClickable(true);
                    }

                    speedText.setText("Speed: 16 bars");


                } else if (instrument.getBars() == 16 && instrument.getSoundList().size() == 16) {


                    final ArrayList<Integer> tempList = instrument.getSoundList();

                    if (tempList.size() == 16) {

                        speedButton.setEnabled(false);
                        speedButton.setClickable(false);
                        speedText.setText("Speed: 8 bars");


                        for (int i = 0; i < 10; i++) {

                            instrument.setBars(8);
                            instrument.setSoundList(
                                    new ArrayList<Integer>(
                                            tempList.subList(0, 8)));
                            instrument.setChangedStatus(true);

                            try {

                                Thread.sleep(CLIENT_UPDATE_TIME / 10);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        speedButton.setEnabled(true);
                        speedButton.setClickable(true);

                    }
                }
                progressText.setText("");

            }
        });
    }

    protected void soundListGUI() {

        soundListText = (TextView) findViewById(R.id.soundListText);

    }

}
