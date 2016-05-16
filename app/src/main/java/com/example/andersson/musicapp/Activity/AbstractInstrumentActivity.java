package com.example.andersson.musicapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

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

    public ThreadHolder threadHolder;
    public BeatHolder beatHolder;
    public MainHolder mainHolder;
    protected SoundPoolHolder soundPoolHolder;

    public AbstractInstrumentThread instrument;

    public EditText soundListText;
    public Button recordButton;
    public Button playButton;
    public Button stopButton;
    public Button removeButton;
    public Button speedButton;
    public TextView speedText;
    public SeekBar volumeSeekBar;
    public TextView progressText;

    protected ArrayList<Integer> tempSoundList;

    private int index = 0;
    private int countDown;
    private String info;

    public boolean playRealTime;
    public boolean record;

    private final static int UPDATE_TIME = 4000;

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

        final ThreadPool threadPool = ThreadPool.getInstance();

        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!record) {

                    soundListText.setText("");

                    while (true) {

                        Calendar calendar = Calendar.getInstance();
                        int second = calendar.get(Calendar.SECOND) + 1;
                        int millisecond = calendar.get(Calendar.MILLISECOND) + 1;
                        int time = (int) (Math.round((second * 1000 + millisecond) / 10.0) * 10);
                        final int tempLoopTime = (int) (Math.round((((MainActivity) mainHolder.getMainActivity()).getLoopTime() * 1000) / 10.0) * 10);
                        final double bars = 8;
                        instrument.setBars(8);

                        if (time % tempLoopTime == 0) {

                            index = 0;
                            tempSoundList = new ArrayList<Integer>();


                            Thread tempThread = new Thread() {

                                public void run() {

                                    record = true;
                                    instrument.setRecord(true);

                                    try {

                                        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                        v.vibrate(3 * 1000);

                                        for (int i = 4; i >= 0; i--) {

                                            countDown = i;

                                            runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {

                                                    progressText.setText("Record start in: " + countDown);

                                                }
                                            });

                                            sleep(1000);

                                        }


                                        while (true) {

                                            index++;

                                            runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {

                                                    progressText.setText("Beat " + index + " of " + bars);
                                                    generateSoundInfo(tempSoundList, index);

                                                }
                                            });

                                            sleep(200);
                                            v.vibrate(50);

                                            if (index == bars) {

                                                break;
                                            }

                                            sleep((long) ((tempLoopTime / bars)) - 200);

                                        }

                                    } catch (InterruptedException e) {

                                        e.printStackTrace();
                                    }

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {

                                            progressText.setText("Update server");

                                            instrument.setSoundList(tempSoundList);
                                            record = false;
                                            instrument.setRecord(false);

                                            try {

                                                sleep(UPDATE_TIME);

                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            instrument.setSoundList(tempSoundList);

                                            progressText.setText("");

                                        }
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


                ThreadPool threadPool = ThreadPool.getInstance();

                Thread volumeThread = new Thread() {

                            @Override
                            public void run() {

                        runOnUiThread(new Runnable() {

                                          @Override
                                          public void run() {

                                              int volume = volumeSeekBar.getProgress();
                                              instrument.setVolume(((float) volume) / 100);

                                              try {

                                                  sleep(UPDATE_TIME);

                                              } catch (InterruptedException e) {
                                                  e.printStackTrace();
                                              }

                                              instrument.setVolume(((float) volume) / 100);

                                          }
                                      });
                    }
                };
                threadPool.add(volumeThread,"volumeThread");
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
                tempList.add(-1);
                beatHolder.clearBeatArray();
                instrument.setSoundList(tempList);
                progressText.setText("Instrument removed.");
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


                if (instrument.getBars() == 8 && instrument.getSoundList().size() == 8) {

                    final ArrayList<Integer> tempList = instrument.getSoundList();

                    if (tempList.size() == 8) {


                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                progressText.setText("Update server");

                                ArrayList<Integer> newList = new ArrayList<Integer>();
                                newList.addAll(tempList);
                                newList.addAll(tempList);

                                speedButton.setEnabled(false);
                                speedButton.setClickable(false);
                                instrument.setBars(16);
                                instrument.setSoundList(newList);

                                try {

                                    Thread.sleep(UPDATE_TIME);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                instrument.setSoundList(newList);
                                instrument.setBars(16);
                                speedButton.setEnabled(true);
                                speedButton.setClickable(true);

                            }
                        });



                    }

                    speedText.setText("Speed: 16 bars");

                } else if (instrument.getBars() == 16 && instrument.getSoundList().size() == 16) {


                    final ArrayList<Integer> tempList = instrument.getSoundList();

                    if (tempList.size() == 16) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                instrument.setBars(8);
                                speedButton.setEnabled(false);
                                speedButton.setClickable(false);
                                instrument.setSoundList(
                                        new ArrayList<Integer>(
                                                tempList.subList(0, 8)));

                                try {

                                    Thread.sleep(4000);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                instrument.setBars(8);
                                instrument.setSoundList(
                                        new ArrayList<Integer>(
                                                tempList.subList(0, 8)));
                                speedButton.setEnabled(true);
                                speedButton.setClickable(true);

                            }
                        });
                    }
                    speedText.setText("Speed: 8 bars");

                }
                progressText.setText("");

            }
        });
    }

    protected void soundListGUI() {

        soundListText = (EditText) findViewById(R.id.soundListText);

    }

}
