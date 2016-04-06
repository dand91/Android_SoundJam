package com.example.andersson.musicapp;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements Serializable {

    private Button exampleButton;
    private ThreadHolder holder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        holder = new ThreadHolder();

        exampleButton = (Button) findViewById(R.id.exampleButton);
        exampleButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this, ExampleActivity1.class);

                ThreadHolder tempHolder =  new ThreadHolder(holder);
                Log.d("Main", "Holder status: " + tempHolder.hasHolder() + " " + tempHolder.toString());
                myIntent.putExtra("holder", tempHolder);
                MainActivity.this.startActivityForResult(myIntent, 10);

            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if(resultCode == RESULT_OK){

                ThreadHolder tempHolder = data.getParcelableExtra("holder");
                Log.d("Main","Holder status: " + tempHolder.hasHolder() + " " + tempHolder.toString());

                this.holder = tempHolder;
                holder.transfer();

            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
