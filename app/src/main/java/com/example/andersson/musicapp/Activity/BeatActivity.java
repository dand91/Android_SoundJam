package com.example.andersson.musicapp.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.andersson.musicapp.R;
import com.example.andersson.musicapp.SharedResources.ThreadHolder;

import java.util.HashMap;
import java.util.Map;

public class BeatActivity extends AppCompatActivity {


    public ImageView bd1, bd2, bd3, bd4, bd5, bd6, bd7, bd8;
    public ImageView hh1, hh2, hh3, hh4, hh5, hh6, hh7, hh8;
    public ImageView sn1, sn2, sn3, sn4, sn5, sn6, sn7, sn8;
    public ThreadHolder holder;
    public Map<String, ImageView> beatMap;
    private String info;

    private Button BassDrumButton;
    private Button snareButton;
    private Button HighHatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat);


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
        
        bd1 = (ImageView) findViewById(R.id.bd1);
        bd2 = (ImageView) findViewById(R.id.bd2);
        bd3 = (ImageView) findViewById(R.id.bd3);
        bd4 = (ImageView) findViewById(R.id.bd4);
        bd5 = (ImageView) findViewById(R.id.bd5);
        bd6 = (ImageView) findViewById(R.id.bd6);
        bd7 = (ImageView) findViewById(R.id.bd7);
        bd8 = (ImageView) findViewById(R.id.bd8);

        hh1 = (ImageView) findViewById(R.id.hh1);
        hh2 = (ImageView) findViewById(R.id.hh2);
        hh3 = (ImageView) findViewById(R.id.hh3);
        hh4 = (ImageView) findViewById(R.id.hh4);
        hh5 = (ImageView) findViewById(R.id.hh5);
        hh6 = (ImageView) findViewById(R.id.hh6);
        hh7 = (ImageView) findViewById(R.id.hh7);
        hh8 = (ImageView) findViewById(R.id.hh8);

        sn1 = (ImageView) findViewById(R.id.sn1);
        sn2 = (ImageView) findViewById(R.id.sn2);
        sn3 = (ImageView) findViewById(R.id.sn3);
        sn4 = (ImageView) findViewById(R.id.sn4);
        sn5 = (ImageView) findViewById(R.id.sn5);
        sn6 = (ImageView) findViewById(R.id.sn6);
        sn7 = (ImageView) findViewById(R.id.sn7);
        sn8 = (ImageView) findViewById(R.id.sn8);

        beatMap = new HashMap<String, ImageView>() {{

            put("bd1", bd1);
            put("bd2", bd2);
            put("bd3", bd3);
            put("bd4", bd4);
            put("bd5", bd5);
            put("bd6", bd6);
            put("bd7", bd7);
            put("bd8", bd8);
            put("hh1", hh1);
            put("hh2", hh2);
            put("hh3", hh3);
            put("hh4", hh4);
            put("hh5", hh5);
            put("hh6", hh6);
            put("hh7", hh7);
            put("hh8", hh8);
            put("sn1", sn1);
            put("sn2", sn2);
            put("sn3", sn3);
            put("sn4", sn4);
            put("sn5", sn5);
            put("sn6", sn6);
            put("sn7", sn7);
            put("sn8", sn8);

        }};

        holder = ThreadHolder.getInstance();

        for (Map.Entry<String, Boolean> entry : holder.getBeatMap().entrySet()) {

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

                bg = ContextCompat.getDrawable(this.getApplicationContext(), R.drawable.cell_shape_red);

            } else {

                bg = ContextCompat.getDrawable(this.getApplicationContext(), R.drawable.cell_shape);
            }

            temp.setBackground(bg);

        }

    }

}
