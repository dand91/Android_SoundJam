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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

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

                Thread tempThread = new Thread() {

                    public void run() {

                        if (!record) {

                            runOnUiThread(() -> {

                                soundListText.setText("");
                                recordButton.setClickable(false);
                                recordButton.setEnabled(false);
                                progressText.setText("Waiting for loop");

                            });

                            while (true) {

                                Calendar calendar = Calendar.getInstance();
                                int second = calendar.get(Calendar.SECOND) + 1;
                                int millisecond = calendar.get(Calendar.MILLISECOND) + 1;
                                int time = (int) (Math.round((second * 1000 + millisecond) / 10.0) * 10);
                                final int tempLoopTime = (int) (Math.round((((MainActivity) mainHolder.getMainActivity()).getLoopTime() * 1000) / 10.0) * 10);
                                final double bars = 8;
                                instrument.setBars(8);

                                if (instrument instanceof AbstractDrumThread) {

                                    runOnUiThread(() -> {

                                        speedText.setText("Speed: 8 bars");

                                    });
                                }

                                if (time % tempLoopTime == 0) {

                                    index = 0;
                                    tempSoundList = new ArrayList<Integer>();

                                    record = true;
                                    instrument.setRecord(true);

                                    try {

                                        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

                                        for (int i = 4; i >= 0; i--) {

                                            final int countDown = i;

                                            runOnUiThread(() -> {

                                                progressText.setText("Record start in: " + countDown);

                                            });

                                            for (int j = 0; j < 2; j++) {

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


                                    playRealTime = false;
                                    instrument.setPlayRealTime(false);
                                    record = false;
                                    instrument.setRecord(false);


                                    if (MainHolder.getInstance().getGroupName() != "noName") {

                                        runOnUiThread(() -> {

                                            progressText.setText("Updating server");
                                        });

                                        for (int i = 0; i < 10; i++) {

                                            instrument.setSoundList(tempSoundList);
                                            instrument.setChangedStatus(true);

                                            try {

                                                sleep(CLIENT_UPDATE_TIME / 10);

                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    } else {

                                        instrument.setSoundList(tempSoundList);

                                    }

                                    runOnUiThread(() -> {

                                        recordButton.setClickable(true);
                                        recordButton.setEnabled(true);

                                    });

                                    break;
                                }
                            }
                        }
                    }
                };

                threadPool.add(tempThread,"record");
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
                
                int volume = volumeSeekBar.getProgress();

                if (MainHolder.getInstance().getGroupName() != "noName") {

                    runOnUiThread(() -> {

                        progressText.setText("Updating server");

                    });

                    for (int i = 0; i < 10; i++) {

                        instrument.setVolume(((float) volume) / 100);
                        instrument.setChangedStatus(true);

                        try {

                            Thread.sleep(CLIENT_UPDATE_TIME / 10);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                } else {

                    instrument.setVolume(((float) volume) / 100);

                }

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

                ArrayList<Integer> tempList = new ArrayList<Integer>();
                tempList.add(Integer.MAX_VALUE);

                Thread tempThread = new Thread() {

                    @Override
                    public void run() {

                        beatHolder.clearBeatArray();


                if (MainHolder.getInstance().getGroupName() != "noName") {

                    if (!instrument.getSoundList().isEmpty()) {

                        runOnUiThread(() -> {

                            soundListText.setText("");
                            progressText.setText("Updating server");
                            removeButton.setClickable(false);
                            removeButton.setEnabled(false);

                        });

                        for (int i = 0; i < 10; i++) {

                            instrument.setSoundList(tempList);
                            instrument.setChangedStatus(true);

                            try {

                                Thread.sleep(CLIENT_UPDATE_TIME / 10);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        runOnUiThread(() -> {

                            removeButton.setClickable(true);
                            removeButton.setEnabled(true);
                        });
                    }

                } else {

                    runOnUiThread(() -> {

                        soundListText.setText("");
                        progressText.setText("Instrument removed");

                    });

                    instrument.setSoundList(tempList);

                }
            }
        };
                ThreadPool.getInstance().add(tempThread,"remove");

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

                if(instrument.getSoundList().size() > 0 && instrument.getSoundList().get(0) != Integer.MAX_VALUE){

                Thread tempThread = new Thread() {

                    public void run() {

                        if (instrument.getBars() == 8 && instrument.getSoundList().size() == 8) {


                            final ArrayList<Integer> tempList = instrument.getSoundList();

                            if (tempList.size() == 8) {

                                runOnUiThread(() -> {

                                    speedText.setText("Speed: 16 bars");

                                });

                                ArrayList<Integer> newList = new ArrayList<Integer>();
                                newList.addAll(tempList);
                                newList.addAll(tempList);

                                if (MainHolder.getInstance().getGroupName() != "noName") {


                                    runOnUiThread(() -> {

                                        progressText.setText("Updating server");
                                        speedButton.setEnabled(false);
                                        speedButton.setClickable(false);
                                    });


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

                                    runOnUiThread(() -> {

                                        speedButton.setEnabled(true);
                                        speedButton.setClickable(true);

                                    });

                                } else {

                                    instrument.setBars(16);
                                    instrument.setSoundList(newList);
                                }
                            }


                            runOnUiThread(() -> {

                                speedText.setText("Speed: 16 bars");

                            });


                        } else if (instrument.getBars() == 16 && instrument.getSoundList().size() == 16) {


                            final ArrayList<Integer> tempList = instrument.getSoundList();

                            if (tempList.size() == 16) {

                                if (MainHolder.getInstance().getGroupName() != "noName") {

                                    runOnUiThread(() -> {

                                        speedText.setText("Speed: 8 bars");
                                        progressText.setText("Updating server");

                                        speedButton.setEnabled(false);
                                        speedButton.setClickable(false);

                                    });

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

                                    runOnUiThread(() -> {

                                        speedButton.setEnabled(true);
                                        speedButton.setClickable(true);
                                    });

                                } else {

                                    instrument.setBars(8);
                                    instrument.setSoundList(
                                            new ArrayList<Integer>(
                                                    tempList.subList(0, 8)));

                                }

                            }

                            runOnUiThread(() -> {

                                speedText.setText("Speed: 8 bars");
                            });

                        }

                        runOnUiThread(() -> {

                            progressText.setText("");

                        });

                    }
                };
                ThreadPool.getInstance().add(tempThread, "speed");
            }
            }
        });
    }

    protected void soundListGUI() {

        soundListText = (TextView) findViewById(R.id.soundListText);

    }

    private void freeze(int time){


        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
