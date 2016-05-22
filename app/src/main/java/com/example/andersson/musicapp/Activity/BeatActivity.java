package com.example.andersson.musicapp.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.andersson.musicapp.Pool.ThreadPool;
import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.BeatHolder;

import java.util.HashMap;
import java.util.Map;

public class BeatActivity extends BaseActivity {


    public ImageView BassDrum1, BassDrum2, BassDrum3, BassDrum4, BassDrum5, BassDrum6, BassDrum7, BassDrum8;
    public ImageView HighHat1, HighHat2, HighHat3, HighHat4, HighHat5, HighHat6, HighHat7, HighHat8;
    public ImageView Snare1, Snare2, Snare3, Snare4, Snare5, Snare6, Snare7, Snare8;
    public Map<String, ImageView> beatMap;

    private Button BassDrumButton;
    private Button snareButton;
    private Button HighHatButton;
    private BeatHolder beatHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat);

        beatHolder = BeatHolder.getInstance();
        beatHolder.addActivity(this);

        HighHatButton = (Button) findViewById(R.id.HighHatButton);
        HighHatButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                Intent myIntent = new Intent(BeatActivity.this, HighHatActivity.class);
                myIntent.putExtra("backInfo", "notback");
                BeatActivity.this.startActivityForResult(myIntent, 10);

            }
        });

        BassDrumButton = (Button) findViewById(R.id.BassDrumButton);
        BassDrumButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                Intent myIntent = new Intent(BeatActivity.this, BassdrumActivity.class);
                myIntent.putExtra("backInfo", "notback");
                BeatActivity.this.startActivityForResult(myIntent, 10);


            }
        });

        snareButton = (Button) findViewById(R.id.SnareButton);
        snareButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                Intent myIntent = new Intent(BeatActivity.this, SnareActivity.class);
                myIntent.putExtra("backInfo", "notback");
                BeatActivity.this.startActivityForResult(myIntent, 10);

            }
        });

        BassDrum1 = (ImageView) findViewById(R.id.BassDrum1);
        BassDrum2 = (ImageView) findViewById(R.id.BassDrum2);
        BassDrum3 = (ImageView) findViewById(R.id.BassDrum3);
        BassDrum4 = (ImageView) findViewById(R.id.BassDrum4);
        BassDrum5 = (ImageView) findViewById(R.id.BassDrum5);
        BassDrum6 = (ImageView) findViewById(R.id.BassDrum6);
        BassDrum7 = (ImageView) findViewById(R.id.BassDrum7);
        BassDrum8 = (ImageView) findViewById(R.id.BassDrum8);

        HighHat1 = (ImageView) findViewById(R.id.HighHat1);
        HighHat2 = (ImageView) findViewById(R.id.HighHat2);
        HighHat3 = (ImageView) findViewById(R.id.HighHat3);
        HighHat4 = (ImageView) findViewById(R.id.HighHat4);
        HighHat5 = (ImageView) findViewById(R.id.HighHat5);
        HighHat6 = (ImageView) findViewById(R.id.HighHat6);
        HighHat7 = (ImageView) findViewById(R.id.HighHat7);
        HighHat8 = (ImageView) findViewById(R.id.HighHat8);

        Snare1 = (ImageView) findViewById(R.id.Snare1);
        Snare2 = (ImageView) findViewById(R.id.Snare2);
        Snare3 = (ImageView) findViewById(R.id.Snare3);
        Snare4 = (ImageView) findViewById(R.id.Snare4);
        Snare5 = (ImageView) findViewById(R.id.Snare5);
        Snare6 = (ImageView) findViewById(R.id.Snare6);
        Snare7 = (ImageView) findViewById(R.id.Snare7);
        Snare8 = (ImageView) findViewById(R.id.Snare8);

        beatMap = new HashMap<String, ImageView>() {{

            put("BassDrum1", BassDrum1);
            put("BassDrum2", BassDrum2);
            put("BassDrum3", BassDrum3);
            put("BassDrum4", BassDrum4);
            put("BassDrum5", BassDrum5);
            put("BassDrum6", BassDrum6);
            put("BassDrum7", BassDrum7);
            put("BassDrum8", BassDrum8);
            put("HighHat1", HighHat1);
            put("HighHat2", HighHat2);
            put("HighHat3", HighHat3);
            put("HighHat4", HighHat4);
            put("HighHat5", HighHat5);
            put("HighHat6", HighHat6);
            put("HighHat7", HighHat7);
            put("HighHat8", HighHat8);
            put("Snare1", Snare1);
            put("Snare2", Snare2);
            put("Snare3", Snare3);
            put("Snare4", Snare4);
            put("Snare5", Snare5);
            put("Snare6", Snare6);
            put("Snare7", Snare7);
            put("Snare8", Snare8);

        }};

        updateBeat();
    }


    public void updateBeat() {

        ThreadPool threadPool = ThreadPool.getInstance();

        Thread tempThread = new Thread() {

            @Override
            public void run() {

                runOnUiThread(() -> {

                    for (Map.Entry<String, Boolean> entry : beatHolder.getBeatMap().entrySet()) {


                        String key = entry.getKey();
                        Boolean value = entry.getValue();

                        ImageView temp = null;

                        try {

                            temp = beatMap.get(key);

                        } catch (NullPointerException e) {

                            Log.e("BeatActivity", "Error while fetching: " + key);
                            System.exit(0);
                        }

                        Drawable bg = null;


                        if (value) {

                            bg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.cell_shape_red);

                        } else {

                            bg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.cell_shape);
                        }

                        try {

                            temp.setBackground(bg);

                        } catch (Exception e) {

                            Log.e("BeatActivity", "Error while setting: " + key);

                        }
                    }
                });
            }
        };

        threadPool.add(tempThread, "updateBeat");
    }
}
